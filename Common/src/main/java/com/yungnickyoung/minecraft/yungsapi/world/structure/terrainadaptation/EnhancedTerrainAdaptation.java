package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation;

import net.minecraft.Util;
import net.minecraft.util.Mth;

/**
 * Extra alternatives to vanilla's TerrainAdjustment (1.19+ only), for use with {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawConfig}.
 * Uses a 3-D kernel matrix to smoothly generate noise values for use when generating terrain around structures.
 */
public abstract class EnhancedTerrainAdaptation {
    public static final EnhancedTerrainAdaptation NONE = new NoneAdaptation();

    /**
     * Whether blocks above a structure piece's bounding box y-value should be carved.
     * Used to give structures some space when they generate within terrain, such as for villages and ancient cities.
     **/
    private final boolean doCarving;

    /**
     * Whether blocks below a structure piece's bounding box y-value should be solid.
     * Used to ensure pieces like village houses don't spawn floating in air.
     */
    private final boolean doBearding;

    /**
     * The length of each dimension in the kernel.
     **/
    private final int kernelSize;

    /**
     * Determines how smoothly kernel values transition from 0 to 1 across the kernel.
     * This is analogous to the standard deviation in Gaussian blur functions.
     */
    private final int kernelDistance;

    /**
     * 3-dimensional kernel for smoothing terrain.
     * Values will vary from near-zero along the edges, to near-1 in the middle.
     * These values are used as noise to modify the noise density at specific positions during terrain generation.
     */
    private final float[] kernel;

    abstract public EnhancedTerrainAdaptationType<?> type();

    EnhancedTerrainAdaptation(int kernelSize, int kernelDistance, boolean doCarving, boolean doBearding) {
        this.kernelSize = kernelSize;
        this.kernelDistance = kernelDistance;
        this.doCarving = doCarving;
        this.doBearding = doBearding;
        int kernelRadius = this.getKernelRadius();
        this.kernel = Util.make(new float[kernelSize * kernelSize * kernelSize], (kernel) -> {
            for (int x = 0; x < kernelSize; ++x) {
                for (int y = 0; y < kernelSize; ++y) {
                    for (int z = 0; z < kernelSize; ++z) {
                        int i = index(x, y, z);
                        double kernelX = x - kernelRadius;
                        double kernelY = y - kernelRadius + 0.5;
                        double kernelZ = z - kernelRadius;
                        kernel[i] = computeKernelValue(kernelX, kernelY, kernelZ);
                    }
                }
            }
        });
    }

    /**
     * Computes the kernel value for a given x-, y-, and z-distance.
     *
     * @return A value from 0 to 1.0.
     */
    private float computeKernelValue(double xDistance, double yDistance, double zDistance) {
        double squaredDistance = Mth.lengthSquared(xDistance, yDistance, zDistance);
        return (float) Math.pow(Math.E, -squaredDistance / this.kernelDistance);
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

    public float[] getKernel() {
        return this.kernel;
    }

    /**
     * Computes the noise density factor at a single location given the provided values.
     *
     * @param xDistance            Distance in the x-axis
     * @param yDistance            Distance in the y-axis
     * @param zDistance            Distance in the z-axis
     * @param yDistanceToBeardBase Distance in the y-axis to the beard base,
     *                             the point at which carving and bearding meet
     * @return Noise density factor due to enhanced terrain adaptation
     */
    public double computeDensityFactor(
            int xDistance,
            int yDistance,
            int zDistance,
            int yDistanceToBeardBase
    ) {
        int kernelRadius = this.getKernelRadius();
        int kernelX = xDistance + kernelRadius;
        int kernelY = yDistance + kernelRadius;
        int kernelZ = zDistance + kernelRadius;
        if (isInKernelRange(kernelX) && isInKernelRange(kernelY) && isInKernelRange(kernelZ)) {
            // Get kernel value for this distance.
            // Returns a value from 0 (high distance) to 1 (zero distance)
            int i = index(kernelX, kernelY, kernelZ);
            float kernelValue = this.getKernel()[i];

            double actualYDistanceToAdjustedBottom = (double) yDistanceToBeardBase + 0.5;

            // Calculate squared distance from point to bottom of piece bounding box.
            // We use the bottom of the box as our target since that is the y-position that determines bearding vs carving.
            double squaredDistance = Mth.lengthSquared(xDistance, actualYDistanceToAdjustedBottom, zDistance);

            /* Calculate multiplier for final noise value.
             * The closer we are (i.e. smaller distance), the greater the amplitude (i.e. more negative number).
             *
             * If we are above the beard base (i.e. yDistanceToBeardBase is positive),
             * then the multiplier is negative, and therefore contributes to carving.
             *
             * If we are below the beard base (i.e. yDistanceToBeardBase is negative),
             * then the multiplier is positive, and therefore contributes to solid terrain.
             */
            double multiplier = -actualYDistanceToAdjustedBottom * Mth.fastInvSqrt(squaredDistance / 2.0) / 2.0;
            if (multiplier > 0 && !this.beards()) return 0;
            if (multiplier < 0 && !this.carves()) return 0;
            return multiplier * kernelValue;
        } else {
            return 0;
        }
    }

    private boolean isInKernelRange(int i) {
        return i >= 0 && i < this.kernelSize;
    }

    private int index(int x, int y, int z) {
        return z * this.kernelSize * this.kernelSize + x * this.kernelSize + y;
    }
}
