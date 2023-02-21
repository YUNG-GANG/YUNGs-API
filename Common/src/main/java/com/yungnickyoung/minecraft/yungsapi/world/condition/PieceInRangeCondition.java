package com.yungnickyoung.minecraft.yungsapi.world.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.SinglePoolElementAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches a specified number of blocks from a given position and checks for a structure piece.
 * Passes if a structure piece is found matching one of the entries from the given list.
 * Note that "yungsapi:all" is an acceptable entry for matching any piece.
 */
public class PieceInRangeCondition extends StructureCondition {
    private static final ResourceLocation ALL = new ResourceLocation(YungsApiCommon.MOD_ID, "all");

    public static final Codec<PieceInRangeCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    ResourceLocation.CODEC.listOf().optionalFieldOf("pieces", new ArrayList<>()).forGetter(conditon -> conditon.matchPieces),
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
        if (matchPieces.isEmpty()) {
            matchPieces.add(ALL); // No pieces specified -> match all pieces
        }
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.PIECE_IN_RANGE;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        // Extract args from context
        StructureTemplateManager templateManager = ctx.structureTemplateManager();
        List<PieceEntry> pieces = ctx.pieces();
        PieceEntry pieceEntry = ctx.pieceEntry();

        // Abort if missing any args
        if (templateManager == null) YungsApiCommon.LOGGER.error("Missing required field 'structureTemplateManager' for piece_in_range condition!");
        if (pieces == null) YungsApiCommon.LOGGER.error("Missing required field 'pieces' for piece_in_range condition!");
        if (pieceEntry == null) YungsApiCommon.LOGGER.error("Missing required field 'pieceEntry' for piece_in_horizontal_direction condition!");
        if (templateManager == null || pieces == null || pieceEntry == null) return false;

        PoolElementStructurePiece piece = pieceEntry.getPiece();
        BoundingBox searchBox = new BoundingBox(
                piece.getBoundingBox().minX() - this.horizontalRange,
                piece.getBoundingBox().minY() - this.belowRange,
                piece.getBoundingBox().minZ() - this.horizontalRange,
                piece.getBoundingBox().maxX() + this.horizontalRange,
                piece.getBoundingBox().maxY() + this.aboveRange,
                piece.getBoundingBox().maxZ() + this.horizontalRange);

        // Check for any matching pieces that satisfy the positional criteria
        for (PieceEntry otherPieceEntry : pieces) {
            PoolElementStructurePiece otherPiece = otherPieceEntry.getPiece();
            if (otherPiece.getElement() instanceof SinglePoolElement singlePoolElement) {
                // If otherPiece has the same bounding box as our starting piece, skip over it, as
                // it is likely just the same piece.
                if (otherPiece.getBoundingBox().equals(piece.getBoundingBox())) {
                    continue;
                }

                StructureTemplate otherStructureTemplate = ((SinglePoolElementAccessor)singlePoolElement).callGetTemplate(templateManager);

                // Iterate our target pieces and check for a match with otherPiece
                for (ResourceLocation matchPieceId : matchPieces) {
                    StructureTemplate structureTemplate = templateManager.getOrCreate(matchPieceId);
                    if (otherStructureTemplate == structureTemplate || matchPieceId.equals(ALL)) {
                        // This is one of the pieces we're searching for, so we test its bounding box.
                        // It must intersect the search box, but not be within the current piece's bounding box.
                        if (otherPiece.getBoundingBox().intersects(searchBox) && !otherPiece.getBoundingBox().intersects(piece.getBoundingBox())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
