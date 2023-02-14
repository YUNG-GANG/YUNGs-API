package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.function.Supplier;

public class AutoRegisterConfiguredFeature extends AutoRegisterEntry<ConfiguredFeature<?, ?>> {
    public static AutoRegisterConfiguredFeature of(Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier) {
        return new AutoRegisterConfiguredFeature(configuredFeatureSupplier);
    }

    private AutoRegisterConfiguredFeature(Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier) {
        super(configuredFeatureSupplier);
    }
}
