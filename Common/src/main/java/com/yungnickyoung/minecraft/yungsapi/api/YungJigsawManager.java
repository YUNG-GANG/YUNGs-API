package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.JigsawManager;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.YungJigsawPoolElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

/**
 * An enhanced alternative to vanilla's {@link JigsawPlacement}.
 * <p>
 * Uses an optimized piece selection algorithm, allowing arbitrarily large pool element weights without a performance cost.
 * Includes additional pool element types to choose from for maximum flexibility when creating structures.
 * </p>
 * <p>
 * The main feature of this algorithm is the addition of the {@link YungJigsawPoolElement} pool element types.
 * These element type support additional behaviors such as limiting piece counts to a certain maximum, bounding possible y-values
 * on a per-piece basis, and enforcing arbitrary piece placement conditions.<br />
 * For more information, see {@link YungJigsawPoolElement} and its subclasses.
 * </p>
 * <p>
 * The {@link YungJigsawPoolElement} pool element subtypes should always be preferred over vanilla types.<br />
 * The MaxCount types are considered deprecated and should no longer be used.
 */
public class YungJigsawManager {
    /**
     * Entrypoint for assembling Jigsaw structures with YUNG's Jigsaw Manager.
     *
     * @param generationContext          The generation context.
     * @param startPool                  The StructureTemplatePool of the starting piece.
     * @param startJigsawNameOptional    An optional Resource Location specifying the Name field of a jigsaw block in the starting pool.
     *                                   If specified, the position of a matching jigsaw block will be used as the structure's starting position
     *                                   when generating the structure. Ancient Cities use this to mark the city center as the starting position.
     * @param maxDepth                   The max distance, in Jigsaw pieces, the structure can generate before stopping.
     * @param startPos                   Position from which generation of this structure will start
     * @param useExpansionHack           Whether boundary adjustments should be performed on this structure.
     *                                   In vanilla, only villages and pillager outposts have this enabled.
     * @param projectStartToHeightmap    Heightmap to use for determining y-position. If provided, the startPos
     *                                   y-coordinate acts as an offset to this heightmap; otherwise, the startPos
     *                                   y-coordinate is an absolute world coordinate.
     * @param maxDistanceFromCenter      The radius of the maximum bounding box for the structure. Typical is 80.
     *                                   May need to be increased if your structure is particularly large.
     * @param maxY                       Optional integer for specifying the max possible y-value of the structure.
     *                                   No pieces of the structure can go above this value, if provided.
     * @param minY                       Optional integer for specifying the min possible y-value of the structure.
     *                                   No pieces of the structure can go below this value, if provided.
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
            Optional<Integer> maxY,
            Optional<Integer> minY
    ) {
        return JigsawManager.assembleJigsawStructure(generationContext, startPool, startJigsawNameOptional, maxDepth,
                startPos, useExpansionHack, projectStartToHeightmap, maxDistanceFromCenter, maxY, minY);
    }
}
