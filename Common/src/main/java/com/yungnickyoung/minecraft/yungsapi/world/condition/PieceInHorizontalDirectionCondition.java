package com.yungnickyoung.minecraft.yungsapi.world.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.SinglePoolElementAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches a specified number of blocks from a given position in a specified direction and checks for a structure piece.
 * Passes if a structure piece is found matching one of the entries from the given list.
 * Note that "yungsapi:*" is an acceptable entry for matching any piece.
 */
public class PieceInHorizontalDirectionCondition extends StructureCondition {
    private static final ResourceLocation ALL = new ResourceLocation(YungsApiCommon.MOD_ID, "all");

    public static final Codec<PieceInHorizontalDirectionCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    ResourceLocation.CODEC.listOf().optionalFieldOf("pieces", new ArrayList<>()).forGetter(conditon -> conditon.matchPieces),
                    Codec.INT.fieldOf("range").forGetter(conditon -> conditon.range),
                    Rotation.CODEC.fieldOf("rotation").forGetter(conditon -> conditon.rotation))
            .apply(builder, PieceInHorizontalDirectionCondition::new));

    private final List<ResourceLocation> matchPieces;

    private final Integer range;

    /**
     * The direction to search in, specified by a rotation. <br />
     * Possible values:
     * <ul>
     *     <li><code>none</code> - The current direction, i.e. the same rotation this piece generated with</li>
     *     <li><code>clockwise_90</code></li>
     *     <li><code>counterclockwise_90</code></li>
     *     <li><code>180</code></li>
     * </ul>
     */
    private final Rotation rotation;

    public PieceInHorizontalDirectionCondition(List<ResourceLocation> pieces, int range, Rotation rotation) {
        this.matchPieces = pieces;
        this.range = range;
        this.rotation = rotation;
        if (matchPieces.isEmpty()) {
            matchPieces.add(ALL); // No pieces specified -> match all pieces
        }
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.PIECE_IN_HORIZONTAL_DIRECTION;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        // Extract args from context
        StructureTemplateManager templateManager = ctx.structureTemplateManager();
        List<PieceEntry> pieces = ctx.pieces();
        Rotation pieceRotation = ctx.rotation();
        PieceEntry pieceEntry = ctx.pieceEntry();

        // Abort if missing any args
        if (templateManager == null) YungsApiCommon.LOGGER.error("Missing required field 'structureTemplateManager' for piece_in_horizontal_direction condition!");
        if (pieces == null) YungsApiCommon.LOGGER.error("Missing required field 'pieces' for piece_in_horizontal_direction condition!");
        if (rotation == null) YungsApiCommon.LOGGER.error("Missing required field 'rotation' for piece_in_horizontal_direction condition!");
        if (pieceEntry == null) YungsApiCommon.LOGGER.error("Missing required field 'pieceEntry' for piece_in_horizontal_direction condition!");
        if (templateManager == null || pieces == null || rotation == null || pieceEntry == null) return false;

        PoolElementStructurePiece piece = pieceEntry.getPiece();
        Rotation searchRotation = pieceRotation.getRotated(this.rotation);
        int negX = 0, negZ = 0, posX = 0, posZ = 0;
        switch (searchRotation) {
            case NONE -> negZ = this.range;
            case CLOCKWISE_90 -> posX = this.range;
            case CLOCKWISE_180 -> posZ = this.range;
            case COUNTERCLOCKWISE_90 -> negX = this.range;
        }

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
                        // This is one of the pieces we're searching for, so we test its bounding box
                        BoundingBox searchBox = new BoundingBox(
                                piece.getBoundingBox().minX() - negX,
                                piece.getBoundingBox().minY(),
                                piece.getBoundingBox().minZ() - negZ,
                                piece.getBoundingBox().maxX() + posX,
                                piece.getBoundingBox().maxY(),
                                piece.getBoundingBox().maxZ() + posZ);
                        if (otherPiece.getBoundingBox().intersects(searchBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
