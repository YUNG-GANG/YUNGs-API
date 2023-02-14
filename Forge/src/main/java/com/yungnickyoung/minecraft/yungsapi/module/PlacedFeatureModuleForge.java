package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPlacedFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of PlacedFeatures.
 */
public class PlacedFeatureModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PlacedFeatureModuleForge::registerPlacedFeatures);
    }

    private static void registerPlacedFeatures(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.PLACED_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach(PlacedFeatureModuleForge::register));
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterPlacedFeature autoRegisterPlacedFeature = (AutoRegisterPlacedFeature) data.object();
        PlacedFeature placedFeature = autoRegisterPlacedFeature.get();
        Registry.register(BuiltinRegistries.PLACED_FEATURE, data.name(), placedFeature);
        data.markProcessed();
    }
}