package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.JigsawManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

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
     *
     * @param generationContext          The generation context.
     * @param startPool                  The StructureTemplatePool of the starting piece.
     * @param startJigsawNameOptional    An optional Resource Location specifying the Name field of a jigsaw block in the starting pool.
     *                                   If specified, the position of a matching jigsaw block will be used as the structure's starting position
     *                                   when generating the structure. I believe Ancient Cities use this to adjust its starting position.
     * @param maxDepth                   The max distance, in Jigsaw pieces, the structure can generate before stopping.
     * @param startPos                   Position from which generation of this structure will start
     * @param useExpansionHack           Whether boundary adjustments should be performed on this structure.
     *                                   In vanilla, only villages and pillager outposts have this enabled.
     * @param projectStartToHeightmap    Heightmap to use, if applicable. If provided, the startPos y-coordinate should be an offset.
     *                                   Otherwise, it should be an absolute world coordinate.
     * @param maxDistanceFromCenter      The radius of the bounding box for the structure. Typical is 80.
     *                                   May need to be increased if your structure is particularly large.
     * @param maxY                       Optional Integer for specifying the max possible y-value of the structure.
     *                                   No pieces of the structure can go above this value, if provided.
     */
    public static Optional<Structure.GenerationStub> assembleJigsawStructure(
            Structure.GenerationContext generationContext,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawNameOptional,
            int maxDepth,
            BlockPos startPos,
            boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            Optional<Integer> maxY
    ) {
        return JigsawManager.assembleJigsawStructure(generationContext, startPool, startJigsawNameOptional, maxDepth, startPos, useExpansionHack, projectStartToHeightmap, maxDistanceFromCenter, maxY);
    }
}
