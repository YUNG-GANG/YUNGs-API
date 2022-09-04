package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.function.Supplier;

public class AutoRegisterConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>> extends AutoRegisterEntry<ConfiguredFeature<FC, F>> {
    public static <FC extends FeatureConfiguration, F extends Feature<FC>> AutoRegisterConfiguredFeature<FC, F> of(Supplier<ConfiguredFeature<FC, F>> configuredFeatureSupplier) {
        return new AutoRegisterConfiguredFeature<>(configuredFeatureSupplier);
    }

    private AutoRegisterConfiguredFeature(Supplier<ConfiguredFeature<FC, F>> configuredFeatureSupplier) {
        super(configuredFeatureSupplier);
    }
}
