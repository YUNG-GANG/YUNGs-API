package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterConfiguredFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Registration of ConfiguredFeatures.
 */
public class ConfiguredFeatureModuleForge {
    /**
     * Registers configured features.
     * This is called from {@link PlacedFeatureModuleForge} to ensure all configured features
     * are registered before the placed features.
     */
    public static void registerConfiguredFeatures() {
        AutoRegistrationManager.CONFIGURED_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(ConfiguredFeatureModuleForge::register);
    }

//    @SuppressWarnings("unchecked")
//    private static <FC extends FeatureConfiguration> void register(AutoRegisterField data) {
    private static void register(AutoRegisterField data) {
//        AutoRegisterConfiguredFeature<FC> autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature<FC>) data.object();
//        Feature<FC> feature = autoRegisterConfiguredFeature.feature();
//        FC featureConfiguration = autoRegisterConfiguredFeature.featureConfiguration();
//        ConfiguredFeature<FC, Feature<FC>> configuredFeature = new ConfiguredFeature<>(feature, featureConfiguration);
//        AutoRegisterConfiguredFeature autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature) data.object();
//        Holder<ConfiguredFeature<?, ?>> holder = BuiltinRegistries.register(
//                BuiltinRegistries.CONFIGURED_FEATURE,
//                data.name(),
//                configuredFeature);
        AutoRegisterConfiguredFeature autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature) data.object();
        // Attach ID if not already attached
        if (autoRegisterConfiguredFeature.id == null) {
            autoRegisterConfiguredFeature.id = data.name();
        }

        autoRegisterConfiguredFeature.register();

//        ConfiguredFeature<?, ?> configuredFeature = autoRegisterConfiguredFeature.get();
//        Holder<ConfiguredFeature<?, ?>> holder = BuiltinRegistries.register(
//                BuiltinRegistries.CONFIGURED_FEATURE,
//                data.name(),
//                configuredFeature);
//        autoRegisterConfiguredFeature.setSupplier(() -> configuredFeature);
//        autoRegisterConfiguredFeature.setHolder(holder);
        data.markProcessed();
    }
}