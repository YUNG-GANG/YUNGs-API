package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.SinglePoolElementAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.PieceEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

/**
 * Searches a specified number of blocks from a given position and checks for a structure piece.
 * Passes if a structure piece is found matching one of the entries from the given list.
 * Note that "yungsapi:*" is an acceptable entry for matching any piece.
 */
public class PieceInRangeCondition extends StructureCondition {
    public static final Codec<PieceInRangeCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    ResourceLocation.CODEC.listOf().fieldOf("pieces").forGetter(conditon -> conditon.matchPieces),
                    Codec.INT.optionalFieldOf("above_range", 0).forGetter(conditon -> conditon.aboveRange),
                    Codec.INT.optionalFieldOf("horizontal_range", 0).forGetter(conditon -> conditon.horizontalRange),
                    Codec.INT.optionalFieldOf("below_range", 0).forGetter(conditon -> conditon.belowRange))
            .apply(builder, PieceInRangeCondition::new));

    private final List<ResourceLocation> matchPieces;

    private final Integer aboveRange;
    private final Integer horizontalRange;
    private final Integer belowRange;

    public PieceInRangeCondition(List<ResourceLocation> pieces, int aboveRange, int horizontalRange, int belowRange) {
        this.matchPieces = pieces;
        this.aboveRange = aboveRange;
        this.horizontalRange = horizontalRange;
        this.belowRange = belowRange;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.PIECE_IN_RANGE;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        // Extract args from context
        BlockPos pos = ctx.pos();
        StructureTemplateManager templateManager = ctx.structureTemplateManager();
        List<PieceEntry> pieces = ctx.pieces();

        // Abort if missing any args
        if (pos == null) YungsApiCommon.LOGGER.error("Missing required field 'pos' for ConditionContext!");
        if (templateManager == null) YungsApiCommon.LOGGER.error("Missing required field 'structureTemplateManager' for ConditionContext!");
        if (pieces == null) YungsApiCommon.LOGGER.error("Missing required field 'pieces' for ConditionContext!");
        if (pos == null || templateManager == null || pieces == null) return false;

        // Check for any matching pieces above the pos provided
        for (PieceEntry pieceEntry : pieces) {
            if (pieceEntry.getPiece().getElement() instanceof SinglePoolElement singlePoolElement) {
                StructureTemplate structureTemplate = ((SinglePoolElementAccessor)singlePoolElement).callGetTemplate(templateManager);
                for (ResourceLocation matchPieceId : matchPieces) {
                    if (structureTemplate == templateManager.getOrCreate(matchPieceId) || matchPieceId.getPath().equals("*")) {
                        // This is one of the pieces we're searching for, so we test its bounding box
                        BoundingBox searchBox = new BoundingBox(
                                pos.getX() - this.horizontalRange,
                                pos.getY() - this.belowRange,
                                pos.getZ() - this.horizontalRange,
                                pos.getX() + this.horizontalRange,
                                pos.getY() + this.aboveRange,
                                pos.getZ() + this.horizontalRange);
                        if (pieceEntry.getPiece().getBoundingBox().intersects(searchBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
