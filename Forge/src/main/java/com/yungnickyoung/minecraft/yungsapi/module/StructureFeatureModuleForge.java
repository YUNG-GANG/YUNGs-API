package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registration of structure features.
 */
public class StructureFeatureModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(StructureFeature.class, StructureFeatureModuleForge::registerStructures);
    }

    private static void registerStructures(RegistryEvent.Register<StructureFeature<?>> event) {
        AutoRegistrationManager.STRUCTURE_FEATURES.stream()
                .filter(data -> !data.processed())
                .forEach((registerData) -> register(registerData, event.getRegistry()));
    }

    private static void register(AutoRegisterField data, IForgeRegistry<StructureFeature<?>> registry) {
        StructureFeature<?> structureFeature = ((StructureFeature<?>) data.object());
        structureFeature.setRegistryName(data.name());
        registry.register(structureFeature);
        data.markProcessed();
    }
}