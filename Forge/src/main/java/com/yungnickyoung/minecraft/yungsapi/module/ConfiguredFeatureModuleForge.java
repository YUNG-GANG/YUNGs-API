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
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfiguredFeatureModuleForge::registerConfiguredFeatures);
    }

    private static void registerConfiguredFeatures(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.CONFIGURED_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(ConfiguredFeatureModuleForge::register));
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterConfiguredFeature autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature) data.object();
        ConfiguredFeature<?, ?> configuredFeature = autoRegisterConfiguredFeature.get();
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, data.name(), configuredFeature);
        data.markProcessed();
    }
}