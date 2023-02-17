package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterConfiguredFeature;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPlacedFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of PlacedFeatures.
 */
public class PlacedFeatureModuleForge {
    public static void processEntries() {
        attachIds();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PlacedFeatureModuleForge::registerConfiguredAndPlacedFeatures);
    }

    /**
     * Attaches IDs to the AutoRegister objects themselves.
     * This is necessary for the "lazy-registration" system necessary for ConfiguredFeatures and PlacedFeatures.
     */
    private static void attachIds() {
        AutoRegistrationManager.CONFIGURED_FEATURES.forEach(data -> {
            AutoRegisterConfiguredFeature<?> autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature<?>) data.object();
            if (autoRegisterConfiguredFeature.id == null) {
                autoRegisterConfiguredFeature.id = data.name();
            }
        });
        AutoRegistrationManager.PLACED_FEATURES.forEach(data -> {
            AutoRegisterPlacedFeature autoRegisterPlacedFeature = (AutoRegisterPlacedFeature) data.object();
            if (autoRegisterPlacedFeature.id == null) {
                autoRegisterPlacedFeature.id = data.name();
            }
        });
    }

    private static void registerConfiguredAndPlacedFeatures(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // First we register all configured features.
            ConfiguredFeatureModuleForge.registerConfiguredFeatures();

            // Next we register all placed features.
            AutoRegistrationManager.PLACED_FEATURES.stream()
                    .filter(data -> !data.processed())
                    .forEach(PlacedFeatureModuleForge::register);
        });
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterPlacedFeature autoRegisterPlacedFeature = (AutoRegisterPlacedFeature) data.object();

        // Attach ID if not already attached (shouldn't be possible but just in case)
        if (autoRegisterPlacedFeature.id == null) {
            autoRegisterPlacedFeature.id = data.name();
        }

        // Register if not yet registered
        autoRegisterPlacedFeature.register();

        data.markProcessed();
    }
}