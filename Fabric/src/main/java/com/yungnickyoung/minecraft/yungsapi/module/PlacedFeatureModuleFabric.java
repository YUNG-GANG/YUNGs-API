package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPlacedFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

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
        PlacedFeature placedFeature = autoRegisterPlacedFeature.get();
        Registry.register(BuiltinRegistries.PLACED_FEATURE, data.name(), placedFeature);
        data.markProcessed();
    }
}