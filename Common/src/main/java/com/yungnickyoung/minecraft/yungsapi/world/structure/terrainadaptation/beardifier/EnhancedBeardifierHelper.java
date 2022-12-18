package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.BeardifierAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.YungJigsawSinglePoolElement;
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

import java.util.List;

public class EnhancedBeardifierHelper {
    public static Beardifier forStructuresInChunk(StructureManager structureManager, ChunkPos chunkPos, Beardifier original) {
        ObjectList<EnhancedBeardifierRigid> enhancedBeardifierRigidList = new ObjectArrayList<>(10);
        ObjectList<EnhancedJigsawJunction> enhancedJunctionList = new ObjectArrayList<>(10);
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
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

            // Use max kernel radius to get list of potentially relevant pieces for this chunk
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

                    // If no terrain adaptation for this piece, we can abort
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
                    // Add rigid for each junction
                    for (JigsawJunction jigsawJunction : poolElementPiece.getJunctions()) {
                        int sourceX = jigsawJunction.getSourceX();
                        int sourceZ = jigsawJunction.getSourceZ();
                        if (sourceX > minX - pieceKernelRadius
                                && sourceZ > minZ - pieceKernelRadius
                                && sourceX < minX + 15 + pieceKernelRadius
                                && sourceZ < minZ + 15 + pieceKernelRadius) {
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

    public static double computeDensity(DensityFunction.FunctionContext ctx, double density, EnhancedBeardifierData data) {
        int x = ctx.blockX();
        int y = ctx.blockY();
        int z = ctx.blockZ();

        while (data.getEnhancedRigidIterator() != null && data.getEnhancedRigidIterator().hasNext()) {
            EnhancedBeardifierRigid rigid = data.getEnhancedRigidIterator().next();
            BoundingBox boundingBox = rigid.box();
            int adjustedMinY = boundingBox.minY() + rigid.groundLevelDelta();
            EnhancedTerrainAdaptation enhancedTerrainAdaptation = rigid.enhancedTerrainAdaptation();

            // Get the distance from the bounding box along each axis
            // If within the bounding box, all of these are simply 0
            int xDistanceToBoundingBox = Math.max(0, Math.max(boundingBox.minX() - x, x - boundingBox.maxX()));
            int zDistanceToBoundingBox = Math.max(0, Math.max(boundingBox.minZ() - z, z - boundingBox.maxZ()));
            int yDistanceToBoundingBox = 0;
            if (enhancedTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
                yDistanceToBoundingBox = Math.max(0, Math.max(adjustedMinY - y, y - boundingBox.maxY()));
            }
            int yDistanceToAdjustedBottom = y - adjustedMinY;

            // Calculate density factor and add to density value
            double densityFactor = 0;
            if (enhancedTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
                densityFactor = enhancedTerrainAdaptation.computeDensityFactor(
                        xDistanceToBoundingBox,
                        yDistanceToBoundingBox,
                        zDistanceToBoundingBox,
                        yDistanceToAdjustedBottom
                ) * 0.8D;
            }

            density += densityFactor;
        }
        data.getEnhancedRigidIterator().back(Integer.MAX_VALUE);

        // Vanilla logic
        while (data.getEnhancedJunctionIterator() != null && data.getEnhancedJunctionIterator().hasNext()) {
            EnhancedJigsawJunction enhancedJigsawJunction = data.getEnhancedJunctionIterator().next();
            JigsawJunction jigsawJunction = enhancedJigsawJunction.jigsawJunction();
            EnhancedTerrainAdaptation enhancedTerrainAdaptation = enhancedJigsawJunction.enhancedTerrainAdaptation();
            int xDistanceToJunction = x - jigsawJunction.getSourceX();
            int yDistanceToJunction = y - jigsawJunction.getSourceGroundY();
            int zDistanceToJunction = z - jigsawJunction.getSourceZ();
            density += enhancedTerrainAdaptation.computeDensityFactor(
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
