package com.yungnickyoung.minecraft.yungsapi.world.structure.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.SinglePoolElementAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.YungJigsawSinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Transforms target piece(s) into specified piece.
 */
public class TransformAction extends StructureAction {
    /**
     * Helper codec for a single template. Taken from {@link SinglePoolElement}.
     */
    private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC =
            Codec.of(TransformAction::encodeTemplate, ResourceLocation.CODEC.map(Either::left));

    public static final Codec<TransformAction> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    TEMPLATE_CODEC.listOf().fieldOf("output").forGetter(action -> action.output),
                    Codec.INT.optionalFieldOf("x_offset", 0).forGetter(action -> action.xOffset),
                    Codec.INT.optionalFieldOf("y_offset", 0).forGetter(action -> action.yOffset),
                    Codec.INT.optionalFieldOf("z_offset", 0).forGetter(action -> action.zOffset))
            .apply(builder, TransformAction::new));

    /**
     * Method for encoding a single template, taken from {@link SinglePoolElement}.
     * Shouldn't actually be necessary since we are only ever decoding.
     */
    private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> either, DynamicOps<T> ops, T data) {
        return either.left().isEmpty()
                ? DataResult.error("yungsapi - Cannot serialize a runtime pool element")
                : ResourceLocation.CODEC.encode(either.left().get(), ops, data);
    }

    private final List<Either<ResourceLocation, StructureTemplate>> output;

    private final int xOffset;

    private final int yOffset;

    private final int zOffset;

    public TransformAction(List<Either<ResourceLocation, StructureTemplate>> output,
                           int xOffset,
                           int yOffset,
                           int zOffset) {
        this.output = output;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    @Override
    public StructureActionType<?> type() {
        return StructureActionType.TRANSFORM;
    }

    @Override
    public void apply(StructureContext ctx, PieceEntry targetPieceEntry) {
        // Extract args from context
        StructureManager structureManager = ctx.structureManager();

        // Abort if missing any args
        if (structureManager == null) {
            YungsApiCommon.LOGGER.error("Missing required field 'structureManager' for transform action!");
            return;
        }

        // Transform piece, copying over most other data
        YungJigsawSinglePoolElement old = (YungJigsawSinglePoolElement) targetPieceEntry.getPiece().getElement();
        WorldgenRandom rand = new WorldgenRandom(new LegacyRandomSource(0));
        rand.setFeatureSeed(targetPieceEntry.getPiece().getPosition().getX(),
                targetPieceEntry.getPiece().getPosition().getY(),
                targetPieceEntry.getPiece().getPosition().getX());

        // Randomly choose output piece
        Either<ResourceLocation, StructureTemplate> newTemplate = this.output.get(rand.nextInt(this.output.size()));
        StructurePoolElement newElement = new YungJigsawSinglePoolElement(newTemplate, ((SinglePoolElementAccessor)old).getProcessors(),
                old.getProjection(), old.name, old.maxCount, old.minRequiredDepth, old.maxPossibleDepth,
                old.isPriority, old.ignoreBounds, old.condition, old.enhancedTerrainAdaptation,
                old.deadendPool, old.modifiers);

        // New piece position
        BlockPos offset = new BlockPos(this.xOffset, this.yOffset, this.zOffset);
        offset = offset.rotate(targetPieceEntry.getPiece().getRotation());
        BlockPos newPos = targetPieceEntry.getPiece().getPosition().offset(offset);

        // New piece bounding box
        BoundingBox newBoundingBox = newElement.getBoundingBox(structureManager, newPos, targetPieceEntry.getPiece().getRotation());
        AABB newAabb = AABB.of(newBoundingBox);
        targetPieceEntry.getBoxOctree().getValue().removeBox(targetPieceEntry.getPieceAabb());
        targetPieceEntry.getBoxOctree().getValue().addBox(newAabb);

        PoolElementStructurePiece newPiece = new PoolElementStructurePiece(
                structureManager,
                newElement,
                newPos,
                targetPieceEntry.getPiece().getGroundLevelDelta(),
                targetPieceEntry.getPiece().getRotation(),
                newBoundingBox
        );

        targetPieceEntry.setPiece(newPiece);
    }
}
