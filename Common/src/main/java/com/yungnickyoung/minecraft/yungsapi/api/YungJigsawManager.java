package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.JigsawManager;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountFeaturePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountLegacySinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountListPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountSinglePoolElement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;

import java.util.Optional;

/**
 * An enhanced alternative to vanilla's {@link JigsawPlacement}.
 * <p>
 * Uses an optimized piece selection algorithm, allowing arbitrarily large pool element weights without a performance cost.
 * Includes additional pool element types to choose from for maximum flexibility when creating structures.
 * </p>
 * <p>
 * The following enhanced pool element types are available:
 * <ul>
 * <li>{@link MaxCountSinglePoolElement}</li>
 * <li>{@link MaxCountLegacySinglePoolElement}</li>
 * <li>{@link MaxCountFeaturePoolElement}</li>
 * <li>{@link MaxCountListPoolElement}</li>
 * </ul>
 * Each of these are identical to their vanilla counterparts, but with the following differences:
 * <ol>
 * <li>A <i>name</i> field is required. This can be any string. It is used as an identifier for keeping track of the
 *   max count of a given entry.
 * <li>A <i>max_count</i> field is required. This defines the maximum number of times an element with this entry's name
 *   can be used in a single instance of the entire structure. If multiple entries share the same name, they
 *   should have matching max_count's as well. A warning will be logged if they do not match, and behavior may be unexpected.</li>
 * </ol>
 */
public class YungJigsawManager {
    /**
     * Entrypoint for assembling Jigsaw structures with YUNG's Jigsaw Manager.
     * You can call this method in the exact same manner as vanilla's {@link JigsawPlacement#addPieces(PieceGeneratorSupplier.Context, JigsawPlacement.PieceFactory, BlockPos, boolean, boolean)}
     *
     * @param jigsawContext              The provided generation context
     * @param pieceFactory               The provided piece factory
     * @param startPos                   Position from which generation of this structure will start
     * @param doBoundaryAdjustments      Whether boundary adjustments should be performed on this structure.
     *                                   In vanilla, only villages and pillager outposts have this enabled.
     * @param useHeightmap               Whether the heightmap should be used to correct piece placement.
     *                                   In vanilla, only villages and pillager outposts have this enabled.
     * @param structureBoundingBoxRadius The radius of the bounding box for the structure. Vanilla default is 80.
     *                                   May need to be increased if your structure is particularly large.
     */
    public static Optional<PieceGenerator<YungJigsawConfig>> assembleJigsawStructure(
            PieceGeneratorSupplier.Context<YungJigsawConfig> jigsawContext,
            JigsawPlacement.PieceFactory pieceFactory,
            BlockPos startPos,
            boolean doBoundaryAdjustments,
            boolean useHeightmap,
            int structureBoundingBoxRadius
    ) {
        return JigsawManager.assembleJigsawStructure(jigsawContext, pieceFactory, startPos, doBoundaryAdjustments, useHeightmap, structureBoundingBoxRadius);
    }
}
