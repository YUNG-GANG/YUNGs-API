package com.yungnickyoung.minecraft.yungsapi.world.structure.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.SinglePoolElementAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.PieceEntry;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawSinglePoolElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

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
                    TEMPLATE_CODEC.listOf().fieldOf("output").forGetter(action -> action.output))
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

    public TransformAction(List<Either<ResourceLocation, StructureTemplate>> output) {
        this.output = output;
    }

    @Override
    public StructureActionType<?> type() {
        return StructureActionType.TRANSFORM;
    }

    @Override
    public void apply(StructureContext ctx, PieceEntry targetPieceEntry) {
        // Transform piece, copying over most other data
        YungJigsawSinglePoolElement old = (YungJigsawSinglePoolElement) targetPieceEntry.getPiece().getElement();
        WorldgenRandom rand = new WorldgenRandom(new LegacyRandomSource(0));
        rand.setFeatureSeed(targetPieceEntry.getPiece().getPosition().getX(),
                targetPieceEntry.getPiece().getPosition().getY(),
                targetPieceEntry.getPiece().getPosition().getX());
        Either<ResourceLocation, StructureTemplate> newTemplate = this.output.get(rand.nextInt(this.output.size()));
        PoolElementStructurePiece newPiece = new PoolElementStructurePiece(
                ctx.structureTemplateManager(),
                new YungJigsawSinglePoolElement(newTemplate, ((SinglePoolElementAccessor)old).getProcessors(),
                        old.getProjection(), old.name, old.maxCount, old.minRequiredDepth, old.maxPossibleDepth,
                        old.isPriority, old.ignoreBounds, old.condition, old.enhancedTerrainAdaptation,
                        old.deadendPool, old.modifiers),
                targetPieceEntry.getPiece().getPosition(),
                targetPieceEntry.getPiece().getGroundLevelDelta(),
                targetPieceEntry.getPiece().getRotation(),
                targetPieceEntry.getPiece().getBoundingBox()
        );

        targetPieceEntry.setPiece(newPiece);
    }
}
