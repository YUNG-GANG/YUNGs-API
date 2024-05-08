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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
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
    private void yungsapi_cancelPlaceEntities(ServerLevelAccessor serverLevelAccessor, BlockPos structurePiecePos, Mirror mirror, Rotation rotation, BlockPos pivot, BoundingBox boundingBox, boolean bl, CallbackInfo ci) {
        StructureProcessingContext ctx = context.get();

        // If structure is not using YUNG's API entity processors, we don't need to do anything
        if (ctx.getStructurePlaceSettings().getProcessors().stream().noneMatch(p -> p instanceof StructureEntityProcessor)) {
            return;
        }

        // Apply processors to entities
        List<StructureTemplate.StructureEntityInfo> processedEntities = processEntityInfoList(serverLevelAccessor, ctx);

        // Spawn the processed entities
        for (StructureTemplate.StructureEntityInfo entityInfo : processedEntities) {
            BlockPos entityBlockPos = entityInfo.blockPos;
            if (ctx.getStructurePlaceSettings().getBoundingBox() == null|| ctx.getStructurePlaceSettings().getBoundingBox().isInside(entityBlockPos)) {
                CompoundTag entityNbt = entityInfo.nbt.copy();
                Vec3 entityPos = entityInfo.pos;
                ListTag listTag = new ListTag();
                listTag.add(DoubleTag.valueOf(entityPos.x));
                listTag.add(DoubleTag.valueOf(entityPos.y));
                listTag.add(DoubleTag.valueOf(entityPos.z));
                entityNbt.put("Pos", listTag);
                entityNbt.remove("UUID");
                tryCreateEntity(serverLevelAccessor, entityNbt).ifPresent((entity) -> {
                    float f = entity.mirror(ctx.getStructurePlaceSettings().getMirror());
                    f += entity.getYRot() - entity.rotate(ctx.getStructurePlaceSettings().getRotation());
                    entity.moveTo(entityPos.x, entityPos.y, entityPos.z, f, entity.getXRot());
                    if (ctx.getStructurePlaceSettings().shouldFinalizeEntities() && entity instanceof Mob) {
                        ((Mob) entity).finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(BlockPos.containing(entityPos)), MobSpawnType.STRUCTURE, null, entityNbt);
                    }

                    serverLevelAccessor.addFreshEntityWithPassengers(entity);
                });
            }
        }

        // Cancel the original entity placement to prevent double-spawning entities.
        // Note that other mods like Porting Lib will still be able to run their own entity processors,
        // as long as the structure being processed is not using YUNG's API entity processors.
        ci.cancel();
    }
//
//    @Redirect(method = "placeEntities", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
//    private boolean yungsapi_adjustIterator(Iterator<StructureTemplate.StructureEntityInfo> iterator) {
//        StructureProcessingContext ctx = context.get();
//
//        // If structure is not using YUNG's API entity processors, return the entity info as-is
//        if (ctx.getStructurePlaceSettings().getProcessors().stream().noneMatch(p -> p instanceof StructureEntityProcessor)) {
//            return iterator.hasNext();
//        }
//
//        return ctx.getIterator().hasNext();
//    }
//
//    @Redirect(method = "placeEntities", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
//    private Object yungsapi_applyEntityProcessors(Iterator<StructureTemplate.StructureEntityInfo> iterator) {
//        StructureProcessingContext ctx = context.get();
//
//        // If structure is not using YUNG's API entity processors, return the entity info as-is
//        if (ctx.getStructurePlaceSettings().getProcessors().stream().noneMatch(p -> p instanceof StructureEntityProcessor)) {
//            return iterator.next();
//        }
//
//        // Otherwise, apply entity processors.
//        // We use an iterator built on a copy of the entity info list in case another mod like Porting Lib modifies the list.
//        StructureTemplate.StructureEntityInfo rawEntityInfo = ctx.getIterator().next();
//
//        // Calculate transformed position so processors have access to the actual global world coordinates of the entity
//        Vec3 globalPos = StructureTemplate
//                .transform(rawEntityInfo.pos,
//                        ctx.getStructurePlaceSettings().getMirror(),
//                        ctx.getStructurePlaceSettings().getRotation(),
//                        ctx.getStructurePlaceSettings().getRotationPivot())
//                .add(Vec3.atLowerCornerOf(ctx.getStructurePiecePos()));
//        BlockPos globalBlockPos = StructureTemplate
//                .transform(rawEntityInfo.blockPos,
//                        ctx.getStructurePlaceSettings().getMirror(),
//                        ctx.getStructurePlaceSettings().getRotation(),
//                        ctx.getStructurePlaceSettings().getRotationPivot())
//                .offset(ctx.getStructurePiecePos());
//        StructureTemplate.StructureEntityInfo globalEntityInfo = new StructureTemplate.StructureEntityInfo(globalPos, globalBlockPos, rawEntityInfo.nbt);
//
//        // Apply processors
//        for (StructureProcessor processor : ctx.getStructurePlaceSettings().getProcessors()) {
//            if (processor instanceof StructureEntityProcessor) {
//                globalEntityInfo = ((StructureEntityProcessor) processor).processEntity(
//                        ctx.getServerLevelAccessor(),
//                        ctx.getStructurePiecePos(),
//                        ctx.getStructurePiecePivotPos(),
//                        rawEntityInfo,
//                        globalEntityInfo,
//                        ctx.getStructurePlaceSettings());
//                if (globalEntityInfo == null) { // null value from processor indicates the entity should not be spawned
//                    // Empty NBT will cause StructureTemplate.getEntity to return an empty Optional, preventing the entity spawn.
//                    return new StructureTemplate.StructureEntityInfo(Vec3.ZERO, BlockPos.ZERO, new CompoundTag());
//                }
//                ((IStructureEntityInfoExtra) globalEntityInfo).yungsapi$setProcessed(true);
//            }
//        }
//
//        return globalEntityInfo;
//    }





    /**
     * Reimplements vanilla behavior for spawning entities,
     * but with additional behavior allowing for the use of entity processing ({@link StructureEntityProcessor})
     */
//    @Inject(
//            method = "placeInWorld",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V"))
//    private void yungsapi_processEntities(ServerLevelAccessor serverLevelAccessor, BlockPos structurePiecePos, BlockPos structurePiecePivotPos, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i, CallbackInfoReturnable<Boolean> cir) {
//        for (StructureTemplate.StructureEntityInfo entityInfo : yungsapi$processEntityInfos(serverLevelAccessor, structurePiecePos, structurePiecePivotPos, structurePlaceSettings, this.entityInfoList)) {
//            BlockPos blockPos = entityInfo.blockPos;
//            if (structurePlaceSettings.getBoundingBox() == null || structurePlaceSettings.getBoundingBox().isInside(blockPos)) {
//                CompoundTag compoundTag = entityInfo.nbt.copy();
//                Vec3 vec3d = entityInfo.pos;
//                ListTag listTag = new ListTag();
//                listTag.add(DoubleTag.valueOf(vec3d.x));
//                listTag.add(DoubleTag.valueOf(vec3d.y));
//                listTag.add(DoubleTag.valueOf(vec3d.z));
//                compoundTag.put("Pos", listTag);
//                compoundTag.remove("UUID");
//                yungsapi$getEntity(serverLevelAccessor, compoundTag).ifPresent((entity) -> {
//                    float f = entity.mirror(structurePlaceSettings.getMirror());
//                    f += entity.getYRot() - entity.rotate(structurePlaceSettings.getRotation());
//                    entity.moveTo(vec3d.x, vec3d.y, vec3d.z, f, entity.getXRot());
//                    if (structurePlaceSettings.shouldFinalizeEntities() && entity instanceof Mob) {
//                        ((Mob) entity).finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(BlockPos.containing(vec3d)), MobSpawnType.STRUCTURE, null, compoundTag);
//                    }
//
//                    serverLevelAccessor.addFreshEntityWithPassengers(entity);
//                });
//            }
//        }
//    }

    /**
     * Cancel spawning entities.
     * This behavior is recreated in {@link #yungsapi_processEntities}
     */
//    @Inject(
//            method = "placeEntities",
//            at = @At(value = "HEAD"),
//            cancellable = true)
//    private void yungsapi_cancelPlaceEntities(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, Mirror mirror, Rotation rotation, BlockPos blockPos2, @Nullable BoundingBox boundingBox, boolean bl, CallbackInfo ci) {
//        ci.cancel();
//    }

    /**
     * Applies placement data and {@link StructureEntityProcessor}s to entities in a structure.
     */
    @Unique
    private List<StructureTemplate.StructureEntityInfo> processEntityInfoList(ServerLevelAccessor serverLevelAccessor, StructureProcessingContext ctx) {
        List<StructureTemplate.StructureEntityInfo> processedEntities = new ArrayList<>();

        BlockPos structurePiecePos = ctx.getStructurePiecePos();
        BlockPos structurePiecePivotPos = ctx.getStructurePiecePivotPos();
        StructurePlaceSettings structurePlaceSettings = ctx.getStructurePlaceSettings();
        List<StructureTemplate.StructureEntityInfo> rawEntityInfos = ctx.getRawEntityInfos();

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
