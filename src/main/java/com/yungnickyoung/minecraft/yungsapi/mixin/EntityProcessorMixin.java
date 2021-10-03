package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.processor.StructureEntityProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
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
@Mixin(Structure.class)
public class EntityProcessorMixin {
    @Shadow
    @Final
    private List<Structure.StructureEntityInfo> entities;

    /**
     * Reimplements vanilla behavior for spawning entities,
     * but with additional behavior allowing for the use of entity processing ({@link StructureEntityProcessor})
     */
    @Inject(
        method = "place(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/structure/StructurePlacementData;Ljava/util/Random;I)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/Structure;spawnEntities(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/BlockMirror;Lnet/minecraft/util/BlockRotation;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockBox;Z)V"))
    private void processEntities(ServerWorldAccess serverWorldAccess, BlockPos structurePiecePos, BlockPos structurePieceBottomCenterPos, StructurePlacementData placementData, Random random, int i, CallbackInfoReturnable<Boolean> cir) {
        for (Structure.StructureEntityInfo entityInfo : processEntityInfos(serverWorldAccess, structurePiecePos, structurePieceBottomCenterPos, placementData, this.entities)) {
            BlockPos blockPos = entityInfo.blockPos;
            if (placementData.getBoundingBox() == null || placementData.getBoundingBox().contains(blockPos)) {
                NbtCompound compoundTag = entityInfo.nbt.copy();
                Vec3d vec3d = entityInfo.pos;
                NbtList listTag = new NbtList();
                listTag.add(NbtDouble.of(vec3d.x));
                listTag.add(NbtDouble.of(vec3d.y));
                listTag.add(NbtDouble.of(vec3d.z));
                compoundTag.put("Pos", listTag);
                compoundTag.remove("UUID");
                getEntity(serverWorldAccess, compoundTag).ifPresent((entity) -> {
                    float f = entity.applyMirror(placementData.getMirror());
                    f += entity.getYaw() - entity.applyRotation(placementData.getRotation());
                    entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f, entity.getPitch());
                    if (placementData.method_27265() && entity instanceof MobEntity) {
                        ((MobEntity)entity).initialize(serverWorldAccess, serverWorldAccess.getLocalDifficulty(new BlockPos(vec3d)), SpawnReason.STRUCTURE, null, compoundTag);
                    }

                    serverWorldAccess.spawnEntityAndPassengers(entity);
                });
            }
        }
    }


    /**
     * Cancel spawning entities.
     * This behavior is recreated in {@link #processEntities}
     */
    @Inject(
        method = "spawnEntities(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/BlockMirror;Lnet/minecraft/util/BlockRotation;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockBox;Z)V",
        at = @At(value = "HEAD"),
        cancellable = true)
    private void cancelSpawnEntities(ServerWorldAccess serverWorldAccess, BlockPos structurePiecePos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos pivot, @Nullable BlockBox area, boolean bl, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * Applies placement data and {@link StructureEntityProcessor}s to entities in a structure.
     */
    private List<Structure.StructureEntityInfo> processEntityInfos(ServerWorldAccess serverWorldAccess,
                                                                   BlockPos structurePiecePos,
                                                                   BlockPos structurePieceBottomCenterPos,
                                                                   StructurePlacementData structurePlacementData,
                                                                   List<Structure.StructureEntityInfo> rawEntityInfos) {
        List<Structure.StructureEntityInfo> processedEntities = new ArrayList<>();
        for (Structure.StructureEntityInfo rawEntityInfo : rawEntityInfos) {
            // Calculate transformed position so processors have access to the actual global world coordinates of the entity
            Vec3d globalPos = Structure
                .transformAround(rawEntityInfo.pos, structurePlacementData.getMirror(), structurePlacementData.getRotation(), structurePlacementData.getPosition())
                .add(Vec3d.of(structurePiecePos));
            BlockPos globalBlockPos = Structure
                .transformAround(rawEntityInfo.blockPos, structurePlacementData.getMirror(), structurePlacementData.getRotation(), structurePlacementData.getPosition())
                .add((structurePiecePos));
            Structure.StructureEntityInfo globalEntityInfo = new Structure.StructureEntityInfo(globalPos, globalBlockPos, rawEntityInfo.nbt);

            // Apply processors
            for (StructureProcessor processor : structurePlacementData.getProcessors()) {
                if (processor instanceof StructureEntityProcessor) {
                    globalEntityInfo = ((StructureEntityProcessor) processor).processEntity(serverWorldAccess, structurePiecePos, structurePieceBottomCenterPos, rawEntityInfo, globalEntityInfo, structurePlacementData);
                    if (globalEntityInfo == null) break;
                }
            }

            if (globalEntityInfo != null) { // null value from processor indicates the entity should not be spawned
                processedEntities.add(globalEntityInfo);
            }
        }

        return processedEntities;
    }

    private static Optional<Entity> getEntity(ServerWorldAccess serverWorldAccess, NbtCompound compoundTag) {
        try {
            return EntityType.getEntityFromNbt(compoundTag, serverWorldAccess.toServerWorld());
        } catch (Exception var3) {
            return Optional.empty();
        }
    }
}