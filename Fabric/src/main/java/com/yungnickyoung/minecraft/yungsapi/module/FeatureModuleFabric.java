package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Registration of features.
 */
public class FeatureModuleFabric {
    public static void init() {
        AutoRegistrationManager.FEATURES.forEach(FeatureModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        Registry.register(Registry.FEATURE, data.name(), (Feature<?>) data.object());
    }
}