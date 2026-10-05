package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride;

import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public interface AquiferOverrideMaskSupplier {
    AquiferOverrideMask getOrCreateAquiferOverrideMask(Supplier<AquiferOverrideMask> aquiferOverrideMaskSupplier);

    BlockState yungsapi_applyAquiferOverride(int x, int y, int z, BlockState state);
}
