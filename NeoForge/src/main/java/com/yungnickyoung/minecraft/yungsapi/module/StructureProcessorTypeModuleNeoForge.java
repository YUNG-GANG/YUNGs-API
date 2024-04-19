package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Registration of StructureProcessorTypes.
 */
public class StructureProcessorTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(StructureProcessorTypeModuleNeoForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(StructureProcessorTypeModuleNeoForge::register));
    }

    private static void register(AutoRegisterField data) {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, data.name(),  (StructureProcessorType<?>) data.object());
        data.markProcessed();
    }
}