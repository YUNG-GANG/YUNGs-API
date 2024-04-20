package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;

/**
 * Registration of StructurePlacementTypes.
 */
public class StructurePlacementTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildSimpleRegistrar(Registries.STRUCTURE_PLACEMENT, AutoRegistrationManager.STRUCTURE_PLACEMENT_TYPES));
    }
}