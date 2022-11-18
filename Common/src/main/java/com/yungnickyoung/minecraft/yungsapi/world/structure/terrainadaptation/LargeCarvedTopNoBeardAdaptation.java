package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation;

import com.mojang.serialization.Codec;

public class LargeCarvedTopNoBeardAdaptation extends EnhancedTerrainAdaptation {
    private static final LargeCarvedTopNoBeardAdaptation INSTANCE = new LargeCarvedTopNoBeardAdaptation();
    public static final Codec<LargeCarvedTopNoBeardAdaptation> CODEC = Codec.unit(() -> INSTANCE);

    public LargeCarvedTopNoBeardAdaptation() {
        super(24, 16, true, false);
    }

    @Override
    public EnhancedTerrainAdaptationType<?> type() {
        return EnhancedTerrainAdaptationType.LARGE_CARVED_TOP_NO_BEARD;
    }
}
