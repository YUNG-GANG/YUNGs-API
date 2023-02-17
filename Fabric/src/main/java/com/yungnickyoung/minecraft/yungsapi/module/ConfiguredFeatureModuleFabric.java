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
public class ConfiguredFeatureModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.CONFIGURED_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(ConfiguredFeatureModuleFabric::register);
    }

//    @SuppressWarnings("unchecked")
//    private static <FC extends FeatureConfiguration> void register(AutoRegisterField data) {
    private static void register(AutoRegisterField data) {
//        AutoRegisterConfiguredFeature<FC> autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature<FC>) data.object();
//        Feature<FC> feature = autoRegisterConfiguredFeature.feature();
//        FC featureConfiguration = autoRegisterConfiguredFeature.featureConfiguration();
//        ConfiguredFeature<FC, Feature<FC>> configuredFeature = new ConfiguredFeature<>(feature, featureConfiguration);
//        Holder<ConfiguredFeature<?, ?>> holder = BuiltinRegistries.register(
//                BuiltinRegistries.CONFIGURED_FEATURE,
//                data.name(),
//                configuredFeature);
        AutoRegisterConfiguredFeature autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature) data.object();
        // Attach ID if not already attached
        if (autoRegisterConfiguredFeature.id == null) {
            autoRegisterConfiguredFeature.id = data.name();
        }

        // Register if not yet registered
        autoRegisterConfiguredFeature.register();
//        if (!autoRegisterConfiguredFeature.isRegistered()) {
//            ConfiguredFeature<?, ?> configuredFeature = autoRegisterConfiguredFeature.get();
//            Holder<ConfiguredFeature<?, ?>> holder = BuiltinRegistries.register(
//                    BuiltinRegistries.CONFIGURED_FEATURE,
//                    data.name(),
//                    configuredFeature);
//            autoRegisterConfiguredFeature.setSupplier(() -> configuredFeature);
//            autoRegisterConfiguredFeature.setHolder(holder);
//        }

        data.markProcessed();
    }
}