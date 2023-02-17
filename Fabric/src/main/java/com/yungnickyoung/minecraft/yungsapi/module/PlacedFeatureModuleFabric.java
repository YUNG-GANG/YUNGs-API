package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPlacedFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;

/**
 * Registration of PlacedFeatures.
 */
public class PlacedFeatureModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.PLACED_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(PlacedFeatureModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterPlacedFeature autoRegisterPlacedFeature = (AutoRegisterPlacedFeature) data.object();

        // Attach ID if not already attached
        if (autoRegisterPlacedFeature.id == null) {
            autoRegisterPlacedFeature.id = data.name();
        }

        // Register if not yet registered
        autoRegisterPlacedFeature.register();

        data.markProcessed();
    }
}