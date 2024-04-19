package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of StructurePlacementTypes.
 */
public class StructurePlacementTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(StructurePlacementTypeModuleNeoForge::commonSetup);
    }

    private static void commonSetup(final RegisterEvent event) {
        event.register(Registries.STRUCTURE_PLACEMENT, helper -> AutoRegistrationManager.STRUCTURE_PLACEMENT_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerPlacementType(data, helper)));
    }

    private static void registerPlacementType(AutoRegisterField data, RegisterEvent.RegisterHelper<StructurePlacementType<?>> helper) {
        helper.register(data.name(), (StructurePlacementType<?>) data.object());
        data.markProcessed();
    }
}