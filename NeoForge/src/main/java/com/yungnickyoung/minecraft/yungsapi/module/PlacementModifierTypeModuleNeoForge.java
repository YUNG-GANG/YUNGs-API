package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of placement modifier types.
 */
public class PlacementModifierTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(PlacementModifierTypeModuleNeoForge::registerPlacementModifierTypes);
    }

    private static void registerPlacementModifierTypes(RegisterEvent event) {
        event.register(Registries.PLACEMENT_MODIFIER_TYPE, helper -> AutoRegistrationManager.PLACEMENT_MODIFIER_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerPlacementModifierType(data, helper)));
    }

    private static void registerPlacementModifierType(AutoRegisterField data, RegisterEvent.RegisterHelper<PlacementModifierType<?>> helper) {
        PlacementModifierType<?> placementModifierType = (PlacementModifierType<?>) data.object();
        helper.register(data.name(), placementModifierType);
        data.markProcessed();
    }
}