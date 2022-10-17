package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registration of features.
 */
public class FeatureModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, FeatureModuleForge::registerFeatures);
    }

    private static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        AutoRegistrationManager.FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach((registerData) -> registerFeature(registerData, event.getRegistry()));
    }

    private static void registerFeature(AutoRegisterField data, IForgeRegistry<Feature<?>> registry) {
        Feature<?> feature = ((Feature<?>) data.object());
        feature.setRegistryName(data.name());
        registry.register(feature);
        data.markProcessed();
    }
}