package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.BeardifierAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawSinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.EnhancedTerrainAdaptation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import com.yungnickyoung.minecraft.yungsapi.mixin.BeardifierMixin;

import java.util.List;

/**
 * A collection of static helper methods intended to be used by {@link BeardifierMixin}.
 */
public class EnhancedBeardifierHelper {
    /**
     * Attaches additional behavior for an Enhanced Beardifier, which is used for {@link EnhancedTerrainAdaptation}s.
     * @param structureManager The StructureManager
     * @param chunkPos The ChunkPos
     * @param original The original vanilla Beardifier, created by {@link Beardifier#forStructuresInChunk(StructureManager, ChunkPos)}
     * @return The original Beardifier vanilla Beardifier, with additional data for Enhanced behaviors
     */
    public static Beardifier forStructuresInChunk(StructureManager structureManager, ChunkPos chunkPos, Beardifier original) {
        ObjectList<EnhancedBeardifierRigid> enhancedBeardifierRigidList = new ObjectArrayList<>(10);
        ObjectList<EnhancedJigsawJunction> enhancedJunctionList = new ObjectArrayList<>(10);
        int chunkMinBlockX = chunkPos.getMinBlockX();
        int chunkMinBlockZ = chunkPos.getMinBlockZ();
        List<StructureStart> structureStarts = structureManager.startsForStructure(chunkPos, structure -> structure instanceof YungJigsawStructure);
        for (StructureStart structureStart : structureStarts) {
            EnhancedTerrainAdaptation structureTerrainAdaptation = ((YungJigsawStructure) structureStart.getStructure()).enhancedTerrainAdaptation;

            // Determine max kernel radius in the structure.
            // Both the structure itself and any pieces may specify an enhanced terrain adaptation, so we must
            // check everything.
            int kernelRadius = structureTerrainAdaptation.getKernelRadius();
            for (StructurePiece structurePiece : structureStart.getPieces()) {
                if (structurePiece instanceof PoolElementStructurePiece poolPiece
                        && poolPiece.getElement() instanceof YungJigsawSinglePoolElement yungElement
                        && yungElement.hasEnhancedTerrainAdaptation()) {
                    kernelRadius = Math.max(kernelRadius, yungElement.getEnhancedTerrainAdaptation().getKernelRadius());
                }
            }

            int maxKernelRadius = kernelRadius;
            if (maxKernelRadius <= 0) {
                continue;
            }

            // Use max kernel radius to get list of nearby pieces for this chunk.
            // A piece is considered nearby if its bounding box, when padded by maxKernelRadius, intersects this chunk.
            List<StructurePiece> nearbyPieces = structureStart.getPieces().stream()
                    .filter(structurePiece -> structurePiece.isCloseToChunk(chunkPos, maxKernelRadius))
                    .toList();

            for (StructurePiece nearbyPiece : nearbyPieces) {
                if (nearbyPiece instanceof PoolElementStructurePiece poolElementPiece) {
                    StructureTemplatePool.Projection projection = poolElementPiece.getElement().getProjection();

                    // Check if piece overrides terrain adaptation
                    EnhancedTerrainAdaptation pieceTerrainAdaptation = structureTerrainAdaptation;
                    if (poolElementPiece.getElement() instanceof YungJigsawSinglePoolElement yungElement && yungElement.hasEnhancedTerrainAdaptation()) {
                        pieceTerrainAdaptation = yungElement.getEnhancedTerrainAdaptation();
                    }

                    // If no terrain adaptation for this piece, we can ignore it
                    if (pieceTerrainAdaptation == EnhancedTerrainAdaptation.NONE) continue;

                    int pieceKernelRadius = pieceTerrainAdaptation.getKernelRadius();

                    // Add rigid for piece
                    if (projection == StructureTemplatePool.Projection.RIGID) {
                        enhancedBeardifierRigidList.add(
                                new EnhancedBeardifierRigid(
                                        poolElementPiece.getBoundingBox(),
                                        pieceTerrainAdaptation,
                                        poolElementPiece.getGroundLevelDelta()
                                )
                        );
                    }

                    // Add rigid for jigsaw junctions within the intersecting piece
                    for (JigsawJunction jigsawJunction : poolElementPiece.getJunctions()) {
                        int sourceX = jigsawJunction.getSourceX();
                        int sourceZ = jigsawJunction.getSourceZ();
                        // Only consider junctions which are intersecting with this chunk (padded by kernel radius)
                        if (sourceX > chunkMinBlockX - pieceKernelRadius
                                && sourceZ > chunkMinBlockZ - pieceKernelRadius
                                && sourceX < chunkMinBlockX + 15 + pieceKernelRadius
                                && sourceZ < chunkMinBlockZ + 15 + pieceKernelRadius) {
                            enhancedJunctionList.add(new EnhancedJigsawJunction(jigsawJunction, pieceTerrainAdaptation));
                        }
                    }
                } else if (structureTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
                    enhancedBeardifierRigidList.add(new EnhancedBeardifierRigid(
                            nearbyPiece.getBoundingBox(),
                            structureTerrainAdaptation,
                            0));
                }
            }
        }

        Beardifier newBeardifier = new Beardifier(((BeardifierAccessor) original).getPieceIterator(), ((BeardifierAccessor) original).getJunctionIterator());
        ((EnhancedBeardifierData) newBeardifier).setEnhancedRigidIterator(enhancedBeardifierRigidList.iterator());
        ((EnhancedBeardifierData) newBeardifier).setEnhancedJunctionIterator(enhancedJunctionList.iterator());
        return newBeardifier;
    }

    /**
     * Computes the updated density value at the given point, accounting for noise contributions from the EnhancedBeardifierData.
     * @param ctx the density FunctionContext
     * @param density The originally computed vanilla density value at this position
     * @param data The {@link EnhancedBeardifierData} to be used in the computation of the new density value.
     * @return The new density value at the given location, accounting for additional noise contributions.
     */
    public static double computeDensity(DensityFunction.FunctionContext ctx, double density, EnhancedBeardifierData data) {
        int x = ctx.blockX();
        int y = ctx.blockY();
        int z = ctx.blockZ();

        while (data.getEnhancedRigidIterator() != null && data.getEnhancedRigidIterator().hasNext()) {
            EnhancedBeardifierRigid rigid = data.getEnhancedRigidIterator().next();
            BoundingBox pieceBoundingBox = rigid.pieceBoundingBox();
            int adjustedPieceMinY = pieceBoundingBox.minY();
            EnhancedTerrainAdaptation pieceTerrainAdaptation = rigid.pieceTerrainAdaptation();

            /* Get the distance from the pieceBoundingBox along each axis.
             * If within the bounding box, all of these are simply 0.
             * Notably, the below equations grab the maximum *positive* distance. I'm not sure why,
             * as it seems a negative distance value would also work in the call to computeDensityFactor.
             * I don't know, I'm just recreating vanilla logic here.
             */
            int xDistanceToBoundingBox = Math.max(0, Math.max(pieceBoundingBox.minX() - x, x - pieceBoundingBox.maxX()));
            int yDistanceToBoundingBox = Math.max(0, Math.max(adjustedPieceMinY - y, y - pieceBoundingBox.maxY()));
            int zDistanceToBoundingBox = Math.max(0, Math.max(pieceBoundingBox.minZ() - z, z - pieceBoundingBox.maxZ()));
            int yDistanceToAdjustedPieceBottom = y - adjustedPieceMinY;

            // Calculate density factor and add to density value
            double densityFactor = 0;
            if (pieceTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
                densityFactor = pieceTerrainAdaptation.computeDensityFactor(
                        xDistanceToBoundingBox,
                        yDistanceToBoundingBox,
                        zDistanceToBoundingBox,
                        yDistanceToAdjustedPieceBottom
                ) * 0.8D;
            }

            density += densityFactor;
        }
        data.getEnhancedRigidIterator().back(Integer.MAX_VALUE);

        // Vanilla logic
        while (data.getEnhancedJunctionIterator() != null && data.getEnhancedJunctionIterator().hasNext()) {
            EnhancedJigsawJunction enhancedJigsawJunction = data.getEnhancedJunctionIterator().next();
            JigsawJunction jigsawJunction = enhancedJigsawJunction.jigsawJunction();
            EnhancedTerrainAdaptation pieceTerrainAdaptation = enhancedJigsawJunction.pieceTerrainAdaptation();
            int xDistanceToJunction = x - jigsawJunction.getSourceX();
            int yDistanceToJunction = y - jigsawJunction.getSourceGroundY();
            int zDistanceToJunction = z - jigsawJunction.getSourceZ();
            density += pieceTerrainAdaptation.computeDensityFactor(
                    xDistanceToJunction,
                    yDistanceToJunction,
                    zDistanceToJunction,
                    yDistanceToJunction
            ) * 0.4D;
        }
        data.getEnhancedJunctionIterator().back(Integer.MAX_VALUE);

        return density;
    }
}
