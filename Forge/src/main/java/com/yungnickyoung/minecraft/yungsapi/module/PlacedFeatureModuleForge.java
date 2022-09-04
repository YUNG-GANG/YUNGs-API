package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPlacedFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of PlacedFeatures.
 */
public class PlacedFeatureModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PlacedFeatureModuleForge::registerPlacedFeatures);
    }

    private static void registerPlacedFeatures(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AutoRegistrationManager.PLACED_FEATURES.forEach(PlacedFeatureModuleForge::registerPlacedFeature);
        });
    }

    private static void registerPlacedFeature(AutoRegisterField data) {
        AutoRegisterPlacedFeature autoRegisterPlacedFeature = (AutoRegisterPlacedFeature) data.object();
        PlacedFeature placedFeature = autoRegisterPlacedFeature.get();
        BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, data.name(), placedFeature);
    }
}