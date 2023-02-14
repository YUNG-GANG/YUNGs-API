package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterConfiguredFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * Registration of ConfiguredFeatures.
 */
public class ConfiguredFeatureModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.CONFIGURED_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(ConfiguredFeatureModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterConfiguredFeature autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature) data.object();
        ConfiguredFeature<?, ?> configuredFeature = autoRegisterConfiguredFeature.get();
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, data.name(), configuredFeature);
        data.markProcessed();
    }
}