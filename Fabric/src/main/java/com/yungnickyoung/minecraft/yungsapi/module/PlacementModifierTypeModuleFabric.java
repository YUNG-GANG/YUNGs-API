package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * Registration of placement modifier types.
 */
public class PlacementModifierTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.PLACEMENT_MODIFIER_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(PlacementModifierTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        Registry.register(Registry.PLACEMENT_MODIFIERS, data.name(), (PlacementModifierType<?>) data.object());
        data.markProcessed();
    }
}