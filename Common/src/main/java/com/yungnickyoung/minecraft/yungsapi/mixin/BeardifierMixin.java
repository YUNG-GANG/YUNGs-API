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
import net.minecraft.Util;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects behavior required for using {@link EnhancedTerrainAdaptation} with {@link YungJigsawStructure}.
 */
@Mixin(Beardifier.class)
public class BeardifierMixin implements EnhancedBeardifierData {
    @Shadow @Final private static int BEARD_KERNEL_SIZE;
    @Shadow @Final public static int BEARD_KERNEL_RADIUS;
    @Unique
    private static final int BEARD_KERNEL_SIZE_SMALL = 24;

    @Unique
    private static final int BEARD_KERNEL_RADIUS_SMALL = BEARD_KERNEL_SIZE_SMALL / 2;

    @Unique
    private static final double BEARD_SMALL_DISTANCE = 4.0;

    @Unique
    private static final float[] BEARD_KERNEL_SMALL = Util.make(new float[BEARD_KERNEL_SIZE_SMALL * BEARD_KERNEL_SIZE_SMALL * BEARD_KERNEL_SIZE_SMALL], (kernel) -> {
        for (int z = 0; z < BEARD_KERNEL_SIZE_SMALL; ++z) {
            for (int x = 0; x < BEARD_KERNEL_SIZE_SMALL; ++x) {
                for (int y = 0; y < BEARD_KERNEL_SIZE_SMALL; ++y) {
                    kernel[z * BEARD_KERNEL_SIZE_SMALL * BEARD_KERNEL_SIZE_SMALL + x * BEARD_KERNEL_SIZE_SMALL + y] =
                            (float) yungsapi_computeBeardContributionSmall(x - BEARD_KERNEL_RADIUS_SMALL, y - BEARD_KERNEL_RADIUS_SMALL, z - BEARD_KERNEL_RADIUS_SMALL);
                }
            }
        }
    });

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
                    int kernelRadius = enhancedTerrainAdaptation.getKernelRadius();
                    for (StructurePiece structurePiece : structureStart.getPieces()) {
                        if (structurePiece.isCloseToChunk(chunkPos, kernelRadius)) {
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
                                    if (sourceX > minX - kernelRadius && sourceZ > minZ - kernelRadius && sourceX < minX + 15 + kernelRadius && sourceZ < minZ + 15 + kernelRadius) {
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
    public void yungsapi_calculateDensity(DensityFunction.FunctionContext ctx, CallbackInfoReturnable<Double> cir) {
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
                case CARVED_TOP_NO_BEARD_SMALL, CARVED_TOP_NO_BEARD_LARGE -> Math.max(0, Math.max(adjustedMinY - y, y - boundingBox.maxY()));
                default -> throw new IncompatibleClassChangeError();
            };

            int yDistanceToAdjustedBottom = y - adjustedMinY;
            double densityFactor = switch (rigid.enhancedTerrainAdaptation()) {
                case NONE -> 0;
                case CARVED_TOP_NO_BEARD_SMALL -> yungsapi_getBeardContributionSmall(xDistanceToBoundingBox,
                        yDistanceToBoundingBox,
                        zDistanceToBoundingBox,
                        yDistanceToAdjustedBottom,
                        enhancedTerrainAdaptation.carves(),
                        enhancedTerrainAdaptation.beards()) * 0.8D;
                case CARVED_TOP_NO_BEARD_LARGE -> yungsapi_getBeardContribution(xDistanceToBoundingBox,
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
            density += yungsapi_getBeardContribution(xDistanceToJunction,
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

    @Unique
    private static double yungsapi_getBeardContribution(
            int xDistanceToBoundingBox,
            int yDistanceToBoundingBox,
            int zDistanceToBoundingBox,
            int yDistanceToAdjustedBottom,
            boolean doCarving,
            boolean doBearding
    ) {
        int kernelX = xDistanceToBoundingBox + BEARD_KERNEL_RADIUS;
        int kernelY = yDistanceToBoundingBox + BEARD_KERNEL_RADIUS;
        int kernelZ = zDistanceToBoundingBox + BEARD_KERNEL_RADIUS;
        if (yungsapi_isInKernelRange(kernelX) && yungsapi_isInKernelRange(kernelY) && yungsapi_isInKernelRange(kernelZ)) {
            // Get kernel value for this distance.
            // Returns a value from 0 (far from box) to 1 (within box)
            float kernelValue = BeardifierAccessor.getBEARD_KERNEL()[kernelZ * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE + kernelX * BEARD_KERNEL_SIZE + kernelY];

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

    @Unique
    private static double yungsapi_getBeardContributionSmall(
            int xDistanceToBoundingBox,
            int yDistanceToBoundingBox,
            int zDistanceToBoundingBox,
            int yDistanceToAdjustedBottom,
            boolean doCarving,
            boolean doBearding
    ) {
        int kernelX = xDistanceToBoundingBox + BEARD_KERNEL_RADIUS_SMALL;
        int kernelY = yDistanceToBoundingBox + BEARD_KERNEL_RADIUS_SMALL;
        int kernelZ = zDistanceToBoundingBox + BEARD_KERNEL_RADIUS_SMALL;
        if (yungsapi_isInSmallKernelRange(kernelX) && yungsapi_isInSmallKernelRange(kernelY) && yungsapi_isInSmallKernelRange(kernelZ)) {
            // Get kernel value for this distance.
            // Returns a value from 0 (far from box) to 1 (within box)
            float kernelValue = BEARD_KERNEL_SMALL[kernelZ * BEARD_KERNEL_SIZE_SMALL * BEARD_KERNEL_SIZE_SMALL + kernelX * BEARD_KERNEL_SIZE_SMALL + kernelY];

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

    @Unique
    private static boolean yungsapi_isInKernelRange(int i) {
        return i >= 0 && i < BEARD_KERNEL_SIZE;
    }

    @Unique
    private static boolean yungsapi_isInSmallKernelRange(int i) {
        return i >= 0 && i < BEARD_KERNEL_SIZE_SMALL;
    }

    @Unique
    private static double yungsapi_computeBeardContributionSmall(int x, int y, int z) {
        return yungsapi_computeBeardContributionSmall(x, (double)y + 0.5D, z);
    }

    private static double yungsapi_computeBeardContributionSmall(int x, double y, int z) {
        double squaredDistance = Mth.lengthSquared(x, y, z);
        return Math.pow(Math.E, -squaredDistance / BEARD_SMALL_DISTANCE);
    }
}
