package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;

/**
 * Registration of StructureProcessorTypes.
 */
public class StructureProcessorTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildSimpleRegistrar(Registries.STRUCTURE_PROCESSOR, AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES));
    }
}