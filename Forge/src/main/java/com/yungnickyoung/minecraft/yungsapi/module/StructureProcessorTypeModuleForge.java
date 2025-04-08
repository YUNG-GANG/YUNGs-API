package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of StructureProcessorTypes.
 */
public class StructureProcessorTypeModuleForge {
    public static void processEntries() {
        YungsApiForge.loadingContextEventBus.addListener(YungsApiForge.buildSimpleRegistrar(Registries.STRUCTURE_PROCESSOR, AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES));
    }
}