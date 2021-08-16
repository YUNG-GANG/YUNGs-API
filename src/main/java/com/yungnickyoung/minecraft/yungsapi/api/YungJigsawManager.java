//package com.yungnickyoung.minecraft.yungsapi.api;
//
//import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.JigsawManager;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.world.level.LevelHeightAccessor;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
//import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
//import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
//import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
//
//import java.util.List;
//import java.util.Random;
//
///**
// * YUNG's Jigsaw Manager.
// *
// * Uses an optimized piece selection algorithm, allowing arbitrarily large pool element weights without a performance cost.
// * Includes additional pool element types to choose from for maximum flexibility when creating structures.
// *
// * The following additional element types are available for use in template pools.
// * - yungsapi:max_count_single_element
// * - yungsapi:max_count_legacy_single_element
// * - yungsapi:max_count_feature_element
// * - yungsapi:max_count_list_element
// *
// * Each of these are identical to their vanilla counterparts, but with the following differences:
// * - A "name" field is required. This can be any string. It is used as an identifier for keeping track of the
// *   max count of a given entry.
// * - A "max_count" field is required. This defines the maximum number of times an element with this entry's name
// *   can be used in a single instance of the entire structure. If multiple entries share the same name, they
// *   should have matching max_count's as well. A warning will be logged if they do not match, and behavior may be unexpected.
// */
//public class YungJigsawManager {
//    /**
//     * Entrypoint for assembling Jigsaw structures with YUNG's Jigsaw Manager.
//     * You can call this method in the exact same manner as vanilla's {@link net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement#addPieces(RegistryAccess, JigsawConfiguration, JigsawPlacement.PieceFactory, ChunkGenerator, StructureManager, BlockPos, StructurePieceAccessor, Random, boolean, boolean, LevelHeightAccessor)}
//     * @param registryAccess DynamicRegistries object passed in during structure start generation
//     * @param jigsawConfig A JigsawConfig describing this jigsaw structure. Analogous to vanilla's {@link JigsawConfiguration}
//     * @param chunkGenerator ChunkGenerator object passed in during structure start generation
//     * @param structureManager TemplateManager object passed in during structure start generation
//     * @param startPos Position from which generation of this structure will start
//     * @param components List of pieces in this structure, usually should be the calling structure start's components field
//     * @param random The calling structure start's Random
//     * @param doBoundaryAdjustments Whether or not boundary adjustments should be performed on this structure.
//     *                              In vanilla, only villages and pillager outposts have this enabled.
//     * @param useHeightmap Whether or not the heightmap should be used to correct piece placement.
//     *                     In vanilla, only villages and pillager outposts have this enabled.
//     */
//    public static void assembleJigsawStructure(
//        RegistryAccess registryAccess,
//        YungJigsawConfig jigsawConfig,
//        ChunkGenerator chunkGenerator,
//        StructureManager structureManager,
//        BlockPos startPos,
//        List<? super PoolElementStructurePiece> components,
//        Random random,
//        boolean doBoundaryAdjustments,
//        boolean useHeightmap,
//        LevelHeightAccessor levelHeightAccessor
//    ) {
//        JigsawManager.assembleJigsawStructure(registryAccess, jigsawConfig, chunkGenerator, structureManager, startPos, components, random, doBoundaryAdjustments, useHeightmap, levelHeightAccessor);
//    }
//}
