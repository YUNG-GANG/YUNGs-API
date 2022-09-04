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
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, FeatureModuleForge::registerFeatures);
    }

    private static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        IForgeRegistry<Feature<?>> registry = event.getRegistry();
        AutoRegistrationManager.FEATURES.forEach((registerData) -> registerFeature(registry, registerData));
    }

    private static void registerFeature(IForgeRegistry<Feature<?>> registry, AutoRegisterField data) {
        Feature<?> feature = ((Feature<?>) data.object());
        feature.setRegistryName(data.name());
        registry.register(feature);
    }
}