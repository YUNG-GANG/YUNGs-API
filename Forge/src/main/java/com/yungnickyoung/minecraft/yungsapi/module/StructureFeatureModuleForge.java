package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registration of structure features.
 */
public class StructureFeatureModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(StructureFeature.class, StructureFeatureModuleForge::registerStructures);
    }

    private static void registerStructures(RegistryEvent.Register<StructureFeature<?>> event) {
        IForgeRegistry<StructureFeature<?>> registry = event.getRegistry();
        AutoRegistrationManager.STRUCTURE_FEATURES.forEach((registerData) -> register(registry, registerData));
    }

    private static void register(IForgeRegistry<StructureFeature<?>> registry, RegisterData data) {
        StructureFeature<?> structureFeature = ((StructureFeature<?>) data.object());
        structureFeature.setRegistryName(data.name());
        registry.register(structureFeature);
    }
}