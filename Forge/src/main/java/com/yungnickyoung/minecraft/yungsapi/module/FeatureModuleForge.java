package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of features.
 */
public class FeatureModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(FeatureModuleForge::registerFeatures);
    }

    private static void registerFeatures(RegisterEvent event) {
        event.register(Registries.FEATURE,helper -> AutoRegistrationManager.FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerFeature(data, helper)));
    }

    private static void registerFeature(AutoRegisterField data, RegisterEvent.RegisterHelper<Feature<?>> helper) {
        Feature<?> feature = (Feature<?>) data.object();
        helper.register(data.name(), feature);
        data.markProcessed();
    }
}