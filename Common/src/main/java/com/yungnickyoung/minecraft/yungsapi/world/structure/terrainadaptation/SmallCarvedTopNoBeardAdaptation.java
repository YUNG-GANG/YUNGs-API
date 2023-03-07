package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation;

import com.mojang.serialization.Codec;

public class SmallCarvedTopNoBeardAdaptation extends EnhancedTerrainAdaptation {
    private static final SmallCarvedTopNoBeardAdaptation INSTANCE = new SmallCarvedTopNoBeardAdaptation();
    public static final Codec<SmallCarvedTopNoBeardAdaptation> CODEC = Codec.unit(() -> INSTANCE);

    public SmallCarvedTopNoBeardAdaptation() {
        super(12, 6, true, false);
    }

    @Override
    public EnhancedTerrainAdaptationType<?> type() {
        return EnhancedTerrainAdaptationType.SMALL_CARVED_TOP_NO_BEARD;
    }
}
