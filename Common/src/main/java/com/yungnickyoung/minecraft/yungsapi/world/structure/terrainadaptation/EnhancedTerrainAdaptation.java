package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;

/**
 * Extra alternatives to vanilla's {@link TerrainAdjustment}.
 * For use with {@link YungJigsawStructure}.
 */
public enum EnhancedTerrainAdaptation implements StringRepresentable {
    NONE("none", false, false, 0, 0),
    CARVED_TOP_NO_BEARD_SMALL("carved_top_no_beard_small", true, false, 12, 6),
    CARVED_TOP_NO_BEARD_LARGE("carved_top_no_beard_large", true, false, 24, 16);

    public static final Codec<EnhancedTerrainAdaptation> CODEC = StringRepresentable.fromEnum(EnhancedTerrainAdaptation::values);
    private final String id;
    private final boolean doCarving;
    private final boolean doBearding;
    private final int kernelSize;
    private final int kernelDistance;
    private final float[] kernelValues;

    EnhancedTerrainAdaptation(String id, boolean doCarving, boolean doBearding, int kernelSize, int kernelDistance) {
        this.id = id;
        this.doCarving = doCarving;
        this.doBearding = doBearding;
        this.kernelSize = kernelSize;
        this.kernelDistance = kernelDistance;
        int kernelRadius = this.getKernelRadius();
        this.kernelValues = Util.make(new float[kernelSize * kernelSize * kernelSize], (kernel) -> {
            for (int z = 0; z < kernelSize; ++z) {
                for (int x = 0; x < kernelSize; ++x) {
                    for (int y = 0; y < kernelSize; ++y) {
                        kernel[z * kernelSize * kernelSize + x * kernelSize + y] = (float) computeBeardContribution(
                                x - kernelRadius,
                                y - kernelRadius,
                                z - kernelRadius,
                                kernelDistance);
                    }
                }
            }
        });
    }

    public String getSerializedName() {
        return this.id;
    }

    public boolean carves() {
        return this.doCarving;
    }

    public boolean beards() {
        return this.doBearding;
    }

    public int getKernelSize() {
        return this.kernelSize;
    }

    public int getKernelRadius() {
        return this.kernelSize / 2;
    }

    public int getKernelDistance() {
        return this.kernelDistance;
    }

    public float[] getKernelValues() {
        return this.kernelValues;
    }

    public double computeDensityFactor(
            int xDistanceToBoundingBox,
            int yDistanceToBoundingBox,
            int zDistanceToBoundingBox,
            int yDistanceToAdjustedBottom
    ) {
        int beardKernelSize = this.getKernelSize();
        int beardKernelRadius = this.getKernelRadius();
        int kernelX = xDistanceToBoundingBox + beardKernelRadius;
        int kernelY = yDistanceToBoundingBox + beardKernelRadius;
        int kernelZ = zDistanceToBoundingBox + beardKernelRadius;
        if (isInKernelRange(kernelX, this.getKernelSize())
                && isInKernelRange(kernelY, this.getKernelSize())
                && isInKernelRange(kernelZ, this.getKernelSize())) {
            // Get kernel value for this distance.
            // Returns a value from 0 (far from box) to 1 (within box)
            float kernelValue = this.getKernelValues()[kernelZ * beardKernelSize * beardKernelSize + kernelX * beardKernelSize + kernelY];

            double actualYDistanceToAdjustedBottom = (double) yDistanceToAdjustedBottom + 0.5;
            double squaredDistance = Mth.lengthSquared(xDistanceToBoundingBox, actualYDistanceToAdjustedBottom, zDistanceToBoundingBox);

            /* Calculate multiplier for final noise value.
             * The closer we are to the box, the greater amplitude (i.e. more negative number).
             * If we are above the bounding box bottom, the multiplier is negative, and therefore contributes to carving.
             * If we are below the bounding box bottom, the multiplier is positive, and therefore contributes to solid terrain.
             */
            double multiplier = -actualYDistanceToAdjustedBottom * Mth.fastInvSqrt(squaredDistance / 2.0) / 2.0;
            if (multiplier > 0 && !this.beards()) return 0;
            if (multiplier < 0 && !this.carves()) return 0;
            return multiplier * kernelValue;
        } else {
            return 0;
        }
    }

    private static boolean isInKernelRange(int i, int beardKernelSize) {
        return i >= 0 && i < beardKernelSize;
    }

    private static double computeBeardContribution(int x, int y, int z, double beardDistance) {
        return computeBeardContribution(x, (double) y + 0.5D, z, beardDistance);
    }

    private static double computeBeardContribution(int x, double y, int z, double beardDistance) {
        double squaredDistance = Mth.lengthSquared(x, y, z);
        return Math.pow(Math.E, -squaredDistance / beardDistance);
    }
}
