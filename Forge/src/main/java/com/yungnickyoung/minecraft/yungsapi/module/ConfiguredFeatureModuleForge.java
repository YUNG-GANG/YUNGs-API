package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterConfiguredFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of ConfiguredFeatures.
 */
public class ConfiguredFeatureModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfiguredFeatureModuleForge::registerConfiguredFeatures);
    }

    private static void registerConfiguredFeatures(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AutoRegistrationManager.CONFIGURED_FEATURES.forEach(ConfiguredFeatureModuleForge::registerConfiguredFeature);
        });
    }

    private static void registerConfiguredFeature(AutoRegisterField data) {
        AutoRegisterConfiguredFeature<?, ?> autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature<?, ?>) data.object();
        ConfiguredFeature<?, ?> configuredFeature = autoRegisterConfiguredFeature.get();
        BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, data.name(), configuredFeature);
    }
}