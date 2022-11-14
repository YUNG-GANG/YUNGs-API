package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation;

import com.mojang.serialization.Codec;
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
    CARVED_TOP_NO_BEARD_LARGE("carved_top_no_beard_large", true, false, 24, 12);

    public static final Codec<EnhancedTerrainAdaptation> CODEC = StringRepresentable.fromEnum(EnhancedTerrainAdaptation::values);
    private final String id;
    private final boolean doCarving;
    private final boolean doBearding;
    private final int kernelSize;
    private final int kernelRadius;

    EnhancedTerrainAdaptation(String id, boolean doCarving, boolean doBearding, int kernelSize, int kernelRadius) {
        this.id = id;
        this.doCarving = doCarving;
        this.doBearding = doBearding;
        this.kernelSize = kernelSize;
        this.kernelRadius = kernelRadius;
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
        return this.kernelRadius;
    }
}
