package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.JigsawManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.List;
import java.util.Random;

/**
 * YUNG's Jigsaw Manager.
 *
 * Uses an optimized piece selection algorithm, allowing arbitrarily large pool element weights without a performance cost.
 * Includes additional pool element types to choose from for maximum flexibility when creating structures.
 *
 * The following additional element types are available for use in template pools.
 * - yungsapi:max_count_single_element
 * - yungsapi:max_count_legacy_single_element
 * - yungsapi:max_count_feature_element
 * - yungsapi:max_count_list_element
 *
 * Each of these are identical to their vanilla counterparts, but with the following differences:
 * - A "name" field is required. This can be any string. It is used as an identifier for keeping track of the
 *   max count of a given entry.
 * - A "max_count" field is required. This defines the maximum number of times an element with this entry's name
 *   can be used in a single instance of the entire structure. If multiple entries share the same name, they
 *   should have matching max_count's as well. A warning will be logged if they do not match, and behavior may be unexpected.
 */
public class YungJigsawManager {
    /**
     * Entrypoint for assembling Jigsaw structures with YUNG's Jigsaw Manager.
     * You can call this method in the exact same manner as vanilla's {@link net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement#addPieces(RegistryAccess, PoolElementStructurePiece, int, JigsawPlacement.PieceFactory, ChunkGenerator, StructureManager, List, Random, LevelHeightAccessor)}
     *
     * @param jigsawContext
     * @param pieceFactory
     * @param startPos                   Position from which generation of this structure will start
     * @param doBoundaryAdjustments      Whether or not boundary adjustments should be performed on this structure.
     *                                   In vanilla, only villages and pillager outposts have this enabled.
     * @param useHeightmap               Whether or not the heightmap should be used to correct piece placement.
     *                                   In vanilla, only villages and pillager outposts have this enabled.
     * @param structureBoundingBoxRadius (Optional) The radius of the bounding box for the structure. Defaults to 80.
     *                                   May need to be increased if your structure is particularly large.
     */
    public static void assembleJigsawStructure(
            PieceGeneratorSupplier.Context<JigsawConfiguration> jigsawContext,
            JigsawPlacement.PieceFactory pieceFactory,
            BlockPos startPos,
            boolean doBoundaryAdjustments,
            boolean useHeightmap,
            int structureBoundingBoxRadius
    ) {
        JigsawManager.assembleJigsawStructure(jigsawContext, pieceFactory, startPos, doBoundaryAdjustments, useHeightmap, structureBoundingBoxRadius);
    }
}
