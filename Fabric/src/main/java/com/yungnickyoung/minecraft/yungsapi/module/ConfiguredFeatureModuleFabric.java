package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterConfiguredFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;

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
        AutoRegisterConfiguredFeature<?> autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature<?>) data.object();

        // Attach ID if not already attached
        if (autoRegisterConfiguredFeature.id == null) {
            autoRegisterConfiguredFeature.id = data.name();
        }

        // Register if not yet registered
        autoRegisterConfiguredFeature.register();

        data.markProcessed();
    }
}