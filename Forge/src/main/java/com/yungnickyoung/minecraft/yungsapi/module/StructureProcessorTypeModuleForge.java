package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of StructureProcessorTypes.
 */
public class StructureProcessorTypeModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(StructureProcessorTypeModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(StructureProcessorTypeModuleForge::register));
    }

    private static void register(RegisterData data) {
        Registry.register(Registry.STRUCTURE_PROCESSOR, data.name(),  (StructureProcessorType<?>) data.object());
        data.markProcessed();
    }
}