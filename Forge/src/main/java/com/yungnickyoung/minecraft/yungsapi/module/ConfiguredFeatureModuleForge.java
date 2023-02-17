package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterConfiguredFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;

/**
 * Registration of ConfiguredFeatures.
 */
public class ConfiguredFeatureModuleForge {
    /**
     * Registers configured features.
     * This is called from {@link PlacedFeatureModuleForge} to ensure all ConfiguredFeatures
     * are registered before PlacedFeatures.
     */
    public static void registerConfiguredFeatures() {
        AutoRegistrationManager.CONFIGURED_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(ConfiguredFeatureModuleForge::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterConfiguredFeature<?> autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature<?>) data.object();

        // Attach ID if not already attached (shouldn't be possible but just in case)
        if (autoRegisterConfiguredFeature.id == null) {
            autoRegisterConfiguredFeature.id = data.name();
        }

        // Register if not yet registered
        autoRegisterConfiguredFeature.register();

        data.markProcessed();
    }
}