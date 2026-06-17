package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;

public enum NoneAquiferOverride implements AquiferOverride {
    INSTANCE,
    ;
    public static final MapCodec<NoneAquiferOverride> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public AquiferOverrideType<?> type() {
        return AquiferOverrideType.NONE;
    }

    @Override
    public BlockState getBlockState(BlockState defaultBlockState) {
        return defaultBlockState;
    }
}
