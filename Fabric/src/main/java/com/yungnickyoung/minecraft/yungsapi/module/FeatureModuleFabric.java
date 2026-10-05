package com.yungnickyoung.minecraft.yungsapi.module;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Registration of features.
 */
public class FeatureModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(FeatureModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, data.name(), (MapCodec<? extends Feature>) data.object());
        data.markProcessed();
    }
}
