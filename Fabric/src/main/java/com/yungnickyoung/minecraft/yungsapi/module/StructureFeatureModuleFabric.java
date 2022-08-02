package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

/**
 * Registration of structure features.
 */
public class StructureFeatureModuleFabric {
    public static void init() {
        AutoRegistrationManager.STRUCTURE_FEATURES.forEach(StructureFeatureModuleFabric::register);
    }

    private static void register(RegisterData data) {
        Registry.register(Registry.STRUCTURE_FEATURE, data.name(), (StructureFeature<?>) data.object());
    }
}