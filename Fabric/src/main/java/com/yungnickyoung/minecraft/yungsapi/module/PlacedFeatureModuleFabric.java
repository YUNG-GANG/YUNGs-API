package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPlacedFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

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
//        if (!autoRegisterPlacedFeature.isRegistered()) {
//            Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder = autoRegisterPlacedFeature.getConfiguredFeatureHolder();
//            List<PlacementModifier> placementModifiers = autoRegisterPlacedFeature.placementModifiers();
//            PlacedFeature placedFeature = new PlacedFeature(Holder.hackyErase(configuredFeatureHolder), List.copyOf(placementModifiers));
//            Holder<PlacedFeature> placedFeatureHolder = BuiltinRegistries.register(
//                    BuiltinRegistries.PLACED_FEATURE,
//                    data.name(),
//                    placedFeature);
//            autoRegisterPlacedFeature.setSupplier(() -> placedFeature);
//            autoRegisterPlacedFeature.setHolder(placedFeatureHolder);
//        }

        data.markProcessed();
    }
}