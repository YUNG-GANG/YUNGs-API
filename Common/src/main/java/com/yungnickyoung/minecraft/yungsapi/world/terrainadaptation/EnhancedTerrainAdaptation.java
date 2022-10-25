package com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;

/**
 * Extra alternatives to vanilla's {@link TerrainAdjustment}.
 * For use with {@link YungJigsawStructure}.
 */
public enum EnhancedTerrainAdaptation implements StringRepresentable {
    NONE("none", false, false),
    CARVED_TOP_NO_BEARD("carved_top_no_beard", true, false);

    public static final Codec<EnhancedTerrainAdaptation> CODEC = StringRepresentable.fromEnum(EnhancedTerrainAdaptation::values);
    private final String id;
    private final boolean doCarving;
    private final boolean doBearding;

    EnhancedTerrainAdaptation(String id, boolean doCarving, boolean doBearding) {
        this.id = id;
        this.doCarving = doCarving;
        this.doBearding = doBearding;
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
}
