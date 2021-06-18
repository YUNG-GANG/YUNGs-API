package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.JigsawManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

/**
 * YUNG's Jigsaw Manager.
 *
 * Uses an optimized piece selection algorithm, allowing arbitrarily large pool element weights without a performance cost.
 * Includes additional pool element types to choose from for maximum flexibility when creating structures.
 *
 * Note that any structures using YUNG's Jigsaw Manager must ONLY use YUNG element_type's in the Jigsaw pool JSON files.
 * Vanilla element_type's are NOT supported. However, there are YUNG analogues for every vanilla element_type.
 *
 * The following element_type's are identical in functionality to their vanilla counterparts, with the exception of
 * additionally requiring a "name" string field.
 * - yungsapi:yung_single_element -------- analogous to vanilla single_pool_element
 * - yungsapi:yung_legacy_single_element - analogous to vanilla legacy_single_pool_element
 * - yungsapi:yung_feature_element ------- analogous to vanilla feature_pool_element
 * - yungsapi:yung_list_element ---------- analogous to vanilla list_pool_element
 *
 * The following element_type's are new types introduced in YUNG's Jigsaw Manager.
 * - yungsapi:yung_max_count_single_element - Same as a yung_single_element, but additionally requires a
 *   max_count integer field, which defines the maximum number of times an element with this element's name
 *   can be used in a single instance of the entire structure.
 *
 * No matter the element_type chosen, be aware that a 'name' string field is always require.
 * This serves as an element identifier necessary for the optimized piece selection algorithm.
 * If you aren't using any yung_max_count_single_element's, then the names won't matter, but they are required nonetheless.
 */
public class YungJigsawManager {
    /**
     * Entrypoint for assembling Jigsaw structures with YUNG's Jigsaw Manager.
     * You can call this method in the exact same manner as vanilla's {@link net.minecraft.world.gen.feature.jigsaw.JigsawManager#func_242837_a(DynamicRegistries, VillageConfig, net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory, ChunkGenerator, TemplateManager, BlockPos, List, Random, boolean, boolean)}
     * @param dynamicRegistryManager DynamicRegistries object passed in during structure start generation
     * @param jigsawConfig A JigsawConfig describing this jigsaw structure. Analogous to vanilla's {@link VillageConfig}
     * @param chunkGenerator ChunkGenerator object passed in during structure start generation
     * @param templateManager TemplateManager object passed in during structure start generation
     * @param startPos Position from which generation of this structure will start
     * @param components List of pieces in this structure, usually should be the calling structure start's components field
     * @param random The calling structure start's Random
     * @param doBoundaryAdjustments Whether or not boundary adjustments should be performed on this structure. In vanilla, only
     *                              villages and pillager outposts have this enabled.
     * @param useHeightmap Whether or not the heightmap should be used to correct piece placement.
     *                     In vanilla, only villages and pillager outposts have this enabled.
     */
    public static void assembleJigsawStructure(
        DynamicRegistries dynamicRegistryManager,
        YungJigsawConfig jigsawConfig,
        ChunkGenerator chunkGenerator,
        TemplateManager templateManager,
        BlockPos startPos,
        List<? super AbstractVillagePiece> components,
        Random random,
        boolean doBoundaryAdjustments,
        boolean useHeightmap
    ) {
        JigsawManager.assembleJigsawStructure(dynamicRegistryManager, jigsawConfig, chunkGenerator, templateManager, startPos, components, random, doBoundaryAdjustments, useHeightmap);
    }
}
