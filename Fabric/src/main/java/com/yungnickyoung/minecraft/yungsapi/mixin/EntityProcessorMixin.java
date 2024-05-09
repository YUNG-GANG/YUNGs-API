package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.processor.StructureEntityProcessor;
import com.yungnickyoung.minecraft.yungsapi.world.processor.StructureProcessingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Allows for processing entities in Jigsaw structures.
 */
@Mixin(StructureTemplate.class)
public class EntityProcessorMixin {
    @Shadow
    @Final
    private List<StructureTemplate.StructureEntityInfo> entityInfoList;

    @Unique
    private static final ThreadLocal<StructureProcessingContext> context = new ThreadLocal<>();

    @Inject(
            method = "placeInWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V"))
    private void yungsapi_captureContext(ServerLevelAccessor serverLevelAccessor, BlockPos structurePiecePos, BlockPos structurePiecePivotPos,
                                         StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i, CallbackInfoReturnable<Boolean> cir) {
        context.set(new StructureProcessingContext(
                serverLevelAccessor,
                structurePlaceSettings,
                structurePiecePos,
                structurePiecePivotPos,
                entityInfoList
        ));
    }

    @Inject(
            method = "placeInWorld",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V"))
    private void yungsapi_clearContext(ServerLevelAccessor serverLevelAccessor, BlockPos structurePiecePos, BlockPos structurePiecePivotPos,
                                       StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i, CallbackInfoReturnable<Boolean> cir) {
        context.remove();
    }

    @Inject(
            method = "placeEntities",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void yungsapi_processAndPlaceEntities(ServerLevelAccessor serverLevelAccessor, BlockPos structurePiecePos, Mirror mirror, Rotation rotation, BlockPos pivot, BoundingBox boundingBox, boolean bl, CallbackInfo ci) {
        StructureProcessingContext ctx = context.get();

        // If structure is not using YUNG's API entity processors, we don't need to do anything
        if (ctx.structurePlaceSettings().getProcessors().stream().noneMatch(p -> p instanceof StructureEntityProcessor)) {
            return;
        }

        // Apply processors to entities
        List<StructureTemplate.StructureEntityInfo> processedEntities = processEntityInfoList(ctx);

        // Place the processed entities
        for (StructureTemplate.StructureEntityInfo entityInfo : processedEntities) {
            BlockPos entityBlockPos = entityInfo.blockPos;
            if (ctx.structurePlaceSettings().getBoundingBox() == null|| ctx.structurePlaceSettings().getBoundingBox().isInside(entityBlockPos)) {
                CompoundTag entityNbt = entityInfo.nbt.copy();
                Vec3 entityPos = entityInfo.pos;
                ListTag listTag = new ListTag();
                listTag.add(DoubleTag.valueOf(entityPos.x));
                listTag.add(DoubleTag.valueOf(entityPos.y));
                listTag.add(DoubleTag.valueOf(entityPos.z));
                entityNbt.put("Pos", listTag);
                entityNbt.remove("UUID");
                tryCreateEntity(serverLevelAccessor, entityNbt).ifPresent((entity) -> {
                    float f = entity.mirror(ctx.structurePlaceSettings().getMirror());
                    f += entity.getYRot() - entity.rotate(ctx.structurePlaceSettings().getRotation());
                    entity.moveTo(entityPos.x, entityPos.y, entityPos.z, f, entity.getXRot());
                    if (ctx.structurePlaceSettings().shouldFinalizeEntities() && entity instanceof Mob) {
                        ((Mob) entity).finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(BlockPos.containing(entityPos)), MobSpawnType.STRUCTURE, null, entityNbt);
                    }

                    serverLevelAccessor.addFreshEntityWithPassengers(entity);
                });
            }
        }

        // Cancel the original entity placement to prevent double-spawning entities.
        // Note that other mods like Porting Lib will still be able to run their own entity processors,
        // as long as the structure being processed is not also using YUNG's API entity processors.
        ci.cancel();
    }

    /**
     * Applies placement data and {@link StructureEntityProcessor}s to entities in a structure.
     *
     * @return A list of processed entities.
     */
    @Unique
    private List<StructureTemplate.StructureEntityInfo> processEntityInfoList(StructureProcessingContext ctx) {
        List<StructureTemplate.StructureEntityInfo> processedEntities = new ArrayList<>();

        // Extract context data
        ServerLevelAccessor serverLevelAccessor = ctx.serverLevelAccessor();
        BlockPos structurePiecePos = ctx.structurePiecePos();
        BlockPos structurePiecePivotPos = ctx.structurePiecePivotPos();
        StructurePlaceSettings structurePlaceSettings = ctx.structurePlaceSettings();
        List<StructureTemplate.StructureEntityInfo> rawEntityInfos = ctx.rawEntityInfos();

        for (StructureTemplate.StructureEntityInfo rawEntityInfo : rawEntityInfos) {
            // Calculate transformed position so processors have access to the actual global world coordinates of the entity
            Vec3 globalPos = StructureTemplate
                    .transform(rawEntityInfo.pos,
                            structurePlaceSettings.getMirror(),
                            structurePlaceSettings.getRotation(),
                            structurePlaceSettings.getRotationPivot())
                    .add(Vec3.atLowerCornerOf(structurePiecePos));
            BlockPos globalBlockPos = StructureTemplate
                    .transform(rawEntityInfo.blockPos,
                            structurePlaceSettings.getMirror(),
                            structurePlaceSettings.getRotation(),
                            structurePlaceSettings.getRotationPivot())
                    .offset(structurePiecePos);
            StructureTemplate.StructureEntityInfo globalEntityInfo = new StructureTemplate.StructureEntityInfo(globalPos, globalBlockPos, rawEntityInfo.nbt);

            // Apply processors
            for (StructureProcessor processor : structurePlaceSettings.getProcessors()) {
                if (processor instanceof StructureEntityProcessor) {
                    globalEntityInfo = ((StructureEntityProcessor) processor).processEntity(serverLevelAccessor,structurePiecePos, structurePiecePivotPos, rawEntityInfo, globalEntityInfo, structurePlaceSettings);
                    if (globalEntityInfo == null) break;
                }
            }

            if (globalEntityInfo != null) { // null value from processor indicates the entity should not be spawned
                processedEntities.add(globalEntityInfo);
            }
        }

        return processedEntities;
    }

    /**
     * Attempts to create an entity from a CompoundTag.
     * If the entity cannot be created, returns an empty Optional.
     */
    @Unique
    private static Optional<Entity> tryCreateEntity(ServerLevelAccessor serverLevelAccessor, CompoundTag compoundTag) {
        try {
            return EntityType.create(compoundTag, serverLevelAccessor.getLevel());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
