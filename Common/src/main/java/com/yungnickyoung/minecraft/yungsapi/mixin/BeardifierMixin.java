package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.BeardifierAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation.beardifier.EnhancedBeardifierRigid;
import com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation.beardifier.EnhancedBeardifierData;
import com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation.beardifier.EnhancedJigsawJunction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects behavior required for using {@link EnhancedTerrainAdaptation} with {@link YungJigsawStructure}.
 */
@Mixin(Beardifier.class)
public class BeardifierMixin implements EnhancedBeardifierData {
    @Unique
    private ObjectListIterator<EnhancedJigsawJunction> enhancedJunctionIterator;
    @Unique
    private ObjectListIterator<EnhancedBeardifierRigid> enhancedRigidIterator;

    @Inject(method = "forStructuresInChunk", at = @At("RETURN"), cancellable = true)
    private static void yungsapi_supportCustomTerrainAdaptations(StructureManager structureManager, ChunkPos chunkPos, CallbackInfoReturnable<Beardifier> cir) {
        ObjectList<EnhancedBeardifierRigid> enhancedBeardifierRigidList = new ObjectArrayList<>(10);
        ObjectList<EnhancedJigsawJunction> enhancedJunctionList = new ObjectArrayList<>(10);
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        structureManager
                .startsForStructure(chunkPos, structure -> structure instanceof YungJigsawStructure yungJigsawStructure
                        && yungJigsawStructure.terrainAdaptation() == TerrainAdjustment.NONE // Enhanced can't be used alongside vanilla
                        && yungJigsawStructure.enhancedTerrainAdaptation != EnhancedTerrainAdaptation.NONE)
                .forEach(structureStart -> {
                    EnhancedTerrainAdaptation enhancedTerrainAdaptation = ((YungJigsawStructure) structureStart.getStructure()).enhancedTerrainAdaptation;
                    for (StructurePiece structurePiece : structureStart.getPieces()) {
                        if (structurePiece.isCloseToChunk(chunkPos, 12)) {
                            if (structurePiece instanceof PoolElementStructurePiece poolElementStructurePiece) {
                                StructureTemplatePool.Projection projection = poolElementStructurePiece.getElement().getProjection();
                                if (projection == StructureTemplatePool.Projection.RIGID) {
                                    enhancedBeardifierRigidList.add(
                                            new EnhancedBeardifierRigid(
                                                    poolElementStructurePiece.getBoundingBox(),
                                                    enhancedTerrainAdaptation,
                                                    poolElementStructurePiece.getGroundLevelDelta()));
                                }
                                for (JigsawJunction jigsawJunction : poolElementStructurePiece.getJunctions()) {
                                    int sourceX = jigsawJunction.getSourceX();
                                    int sourceZ = jigsawJunction.getSourceZ();
                                    if (sourceX > minX - 12 && sourceZ > minZ - 12 && sourceX < minX + 15 + 12 && sourceZ < minZ + 15 + 12) {
                                        enhancedJunctionList.add(new EnhancedJigsawJunction(jigsawJunction, enhancedTerrainAdaptation));
                                    }
                                }
                            } else {
                                enhancedBeardifierRigidList.add(
                                        new EnhancedBeardifierRigid(
                                                structurePiece.getBoundingBox(),
                                                enhancedTerrainAdaptation,
                                                0));
                            }
                        }
                    }
                });

        Beardifier beardifier = new Beardifier(((BeardifierAccessor) cir.getReturnValue()).getPieceIterator(), ((BeardifierAccessor) cir.getReturnValue()).getJunctionIterator());
        ((EnhancedBeardifierData) beardifier).setEnhancedRigidIterator(enhancedBeardifierRigidList.iterator());
        ((EnhancedBeardifierData) beardifier).setEnhancedJunctionIterator(enhancedJunctionList.iterator());
        cir.setReturnValue(beardifier);
    }

    @Inject(method = "compute", at = @At("RETURN"), cancellable = true)
    public void compute(DensityFunction.FunctionContext ctx, CallbackInfoReturnable<Double> cir) {
        int x = ctx.blockX();
        int y = ctx.blockY();
        int z = ctx.blockZ();
        double density = cir.getReturnValue();

        while (this.enhancedRigidIterator != null && this.enhancedRigidIterator.hasNext()) {
            EnhancedBeardifierRigid rigid = this.enhancedRigidIterator.next();
            BoundingBox boundingBox = rigid.box();
            int adjustedMinY = boundingBox.minY() + rigid.groundLevelDelta();
            EnhancedTerrainAdaptation enhancedTerrainAdaptation = rigid.enhancedTerrainAdaptation();

            // Get the distance from the bounding box along each axis
            // If within the bounding box, all of these are simply 0
            int xDistanceToBoundingBox = Math.max(0, Math.max(boundingBox.minX() - x, x - boundingBox.maxX()));
            int zDistanceToBoundingBox = Math.max(0, Math.max(boundingBox.minZ() - z, z - boundingBox.maxZ()));
            int yDistanceToBoundingBox = switch (enhancedTerrainAdaptation) {
                case NONE -> 0;
                case CARVED_TOP_NO_BEARD -> Math.max(0, Math.max(adjustedMinY - y, y - boundingBox.maxY()));
                default -> throw new IncompatibleClassChangeError();
            };

            int yDistanceToAdjustedBottom = y - adjustedMinY;
            double densityFactor = switch (rigid.enhancedTerrainAdaptation()) {
                case NONE -> 0;
                case CARVED_TOP_NO_BEARD -> getBeardContribution(xDistanceToBoundingBox,
                        yDistanceToBoundingBox,
                        zDistanceToBoundingBox,
                        yDistanceToAdjustedBottom,
                        enhancedTerrainAdaptation.carves(),
                        enhancedTerrainAdaptation.beards()) * 0.8D;
                default -> throw new IncompatibleClassChangeError();
            };

            density += densityFactor;
        }
        this.enhancedRigidIterator.back(Integer.MAX_VALUE);

        // Vanilla logic
        while (this.enhancedJunctionIterator != null && this.enhancedJunctionIterator.hasNext()) {
            EnhancedJigsawJunction enhancedJigsawJunction = this.enhancedJunctionIterator.next();
            JigsawJunction jigsawJunction = enhancedJigsawJunction.jigsawJunction();
            EnhancedTerrainAdaptation enhancedTerrainAdaptation = enhancedJigsawJunction.enhancedTerrainAdaptation();
            int xDistanceToJunction = x - jigsawJunction.getSourceX();
            int yDistanceToJunction = y - jigsawJunction.getSourceGroundY();
            int zDistanceToJunction = z - jigsawJunction.getSourceZ();
            density += getBeardContribution(xDistanceToJunction,
                    yDistanceToJunction,
                    zDistanceToJunction,
                    yDistanceToJunction,
                    enhancedTerrainAdaptation.carves(),
                    enhancedTerrainAdaptation.beards()) * 0.4D;
        }
        this.enhancedJunctionIterator.back(Integer.MAX_VALUE);

        cir.setReturnValue(density);
    }

    @Override
    public ObjectListIterator<EnhancedBeardifierRigid> getEnhancedRigidIterator() {
        return this.enhancedRigidIterator;
    }

    @Override
    public void setEnhancedRigidIterator(ObjectListIterator<EnhancedBeardifierRigid> enhancedRigidIterator) {
        this.enhancedRigidIterator = enhancedRigidIterator;
    }

    @Override
    public ObjectListIterator<EnhancedJigsawJunction> getEnhancedJunctionIterator() {
        return enhancedJunctionIterator;
    }

    @Override
    public void setEnhancedJunctionIterator(ObjectListIterator<EnhancedJigsawJunction> enhancedJunctionIterator) {
        this.enhancedJunctionIterator = enhancedJunctionIterator;
    }

    private static double getBeardContribution(
            int xDistanceToBoundingBox,
            int yDistanceToBoundingBox,
            int zDistanceToBoundingBox,
            int yDistanceToAdjustedBottom,
            boolean doCarving,
            boolean doBearding
    ) {
        int kernelX = xDistanceToBoundingBox + 12;
        int kernelY = yDistanceToBoundingBox + 12;
        int kernelZ = zDistanceToBoundingBox + 12;
        if (isInKernelRange(kernelX) && isInKernelRange(kernelY) && isInKernelRange(kernelZ)) {
            // Get kernel value for this distance.
            // Returns a value from 0 (far from box) to 1 (within box)
            float kernelValue = BeardifierAccessor.getBEARD_KERNEL()[kernelZ * 24 * 24 + kernelX * 24 + kernelY];

            double actualYDistanceToAdjustedBottom = (double) yDistanceToAdjustedBottom + 0.5;
            double squaredDistance = Mth.lengthSquared(xDistanceToBoundingBox, actualYDistanceToAdjustedBottom, zDistanceToBoundingBox);

            /* Calculate multiplier for final noise value.
             * The closer we are to the box, the greater amplitude (i.e. more negative number).
             * If we are above the bounding box bottom, the multiplier is negative, and therefore contributes to carving.
             * If we are below the bounding box bottom, the multiplier is positive, and therefore contributes to solid terrain.
             */
            double multiplier = -actualYDistanceToAdjustedBottom * Mth.fastInvSqrt(squaredDistance / 2.0) / 2.0;
            if (multiplier > 0 && !doBearding) return 0;
            if (multiplier < 0 && !doCarving) return 0;
            return multiplier * kernelValue;
        } else {
            return 0;
        }
    }

    private static boolean isInKernelRange(int i) {
        return i >= 0 && i < 24;
    }
}
