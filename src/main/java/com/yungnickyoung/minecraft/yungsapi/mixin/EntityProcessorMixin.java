package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.processor.StructureEntityProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Allows for processing entities in Jigsaw structures.
 */
@Mixin(StructureTemplate.class)
public class EntityProcessorMixin {
    @Shadow
    @Final
    private List<StructureTemplate.StructureEntityInfo> entityInfoList;

    /**
     * Reimplements vanilla behavior for spawning entities,
     * but with additional behavior allowing for the use of entity processing ({@link StructureEntityProcessor})
     */
    @Inject(
        method = "placeInWorld",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V"))
    private void processEntities(ServerLevelAccessor serverLevelAccessor, BlockPos structurePiecePos, BlockPos structurePieceBottomCenterPos, StructurePlaceSettings structurePlaceSettings, Random random, int i, CallbackInfoReturnable<Boolean> cir) {
        for (StructureTemplate.StructureEntityInfo entityInfo : processEntityInfos(serverLevelAccessor, structurePiecePos, structurePieceBottomCenterPos, structurePlaceSettings, this.entityInfoList)) {
            BlockPos blockPos = entityInfo.blockPos;
            if (structurePlaceSettings.getBoundingBox() == null || structurePlaceSettings.getBoundingBox().isInside(blockPos)) {
                CompoundTag compoundTag = entityInfo.nbt.copy();
                Vec3 vec3d = entityInfo.pos;
                ListTag listTag = new ListTag();
                listTag.add(DoubleTag.valueOf(vec3d.x));
                listTag.add(DoubleTag.valueOf(vec3d.y));
                listTag.add(DoubleTag.valueOf(vec3d.z));
                compoundTag.put("Pos", listTag);
                compoundTag.remove("UUID");
                getEntity(serverLevelAccessor, compoundTag).ifPresent((entity) -> {
                    float f = entity.mirror(structurePlaceSettings.getMirror());
                    f += entity.getYRot() - entity.rotate(structurePlaceSettings.getRotation());
                    entity.moveTo(vec3d.x, vec3d.y, vec3d.z, f, entity.getXRot());
                    if (structurePlaceSettings.shouldFinalizeEntities() && entity instanceof Mob) {
                        ((Mob)entity).finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(new BlockPos(vec3d)), MobSpawnType.STRUCTURE, null, compoundTag);
                    }

                    serverLevelAccessor.addFreshEntityWithPassengers(entity);
                });
            }
        }
    }


    /**
     * Cancel spawning entities.
     * This behavior is recreated in {@link #processEntities}
     */
    @Inject(
        method = "placeEntities",
        at = @At(value = "HEAD"),
        cancellable = true)
    private void cancelPlaceEntities(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, Mirror mirror, Rotation rotation, BlockPos blockPos2, @Nullable BoundingBox boundingBox, boolean bl, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * Applies placement data and {@link StructureEntityProcessor}s to entities in a structure.
     */
    private List<StructureTemplate.StructureEntityInfo> processEntityInfos(ServerLevelAccessor serverLevelAccessor,
                                                                   BlockPos structurePiecePos,
                                                                   BlockPos structurePieceBottomCenterPos,
                                                                   StructurePlaceSettings structurePlaceSettings,
                                                                   List<StructureTemplate.StructureEntityInfo> rawEntityInfos) {
        List<StructureTemplate.StructureEntityInfo> processedEntities = new ArrayList<>();
        for (StructureTemplate.StructureEntityInfo rawEntityInfo : rawEntityInfos) {
            // Calculate transformed position so processors have access to the actual global world coordinates of the entity
            Vec3 globalPos = StructureTemplate
                .transform(rawEntityInfo.pos, structurePlaceSettings.getMirror(), structurePlaceSettings.getRotation(), structurePlaceSettings.getRotationPivot())
                .add(Vec3.atLowerCornerOf(structurePiecePos));
            BlockPos globalBlockPos = StructureTemplate
                .transform(rawEntityInfo.blockPos, structurePlaceSettings.getMirror(), structurePlaceSettings.getRotation(), structurePlaceSettings.getRotationPivot())
                .offset(structurePiecePos);
            StructureTemplate.StructureEntityInfo globalEntityInfo = new StructureTemplate.StructureEntityInfo(globalPos, globalBlockPos, rawEntityInfo.nbt);

            // Apply processors
            for (StructureProcessor processor : structurePlaceSettings.getProcessors()) {
                if (processor instanceof StructureEntityProcessor) {
                    globalEntityInfo = ((StructureEntityProcessor) processor).processEntity(serverLevelAccessor, structurePiecePos, structurePieceBottomCenterPos, rawEntityInfo, globalEntityInfo, structurePlaceSettings);
                    if (globalEntityInfo == null) break;
                }
            }

            if (globalEntityInfo != null) { // null value from processor indicates the entity should not be spawned
                processedEntities.add(globalEntityInfo);
            }
        }

        return processedEntities;
    }

    private static Optional<Entity> getEntity(ServerLevelAccessor serverLevelAccessor, CompoundTag compoundTag) {
        try {
            return EntityType.create(compoundTag, serverLevelAccessor.getLevel());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}