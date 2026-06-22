package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverride;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.NoneAquiferOverride;

public class NoneAdaptation extends EnhancedTerrainAdaptation {
    public static final NoneAdaptation INSTANCE = new NoneAdaptation();
    public static final MapCodec<NoneAdaptation> CODEC = MapCodec.unit(() -> INSTANCE);

    private NoneAdaptation() {
        super(0, 0, TerrainAction.NONE, TerrainAction.NONE, 0, Padding.ZERO, NoneAquiferOverride.INSTANCE);
    }

    @Override
    public EnhancedTerrainAdaptationType<?> type() {
        return EnhancedTerrainAdaptationType.NONE;
    }

    @Override
    public double computeDensityFactor(int xDistance, int yDistance, int zDistance, int yDistanceToPieceBottom) {
        return 0;
    }
}
