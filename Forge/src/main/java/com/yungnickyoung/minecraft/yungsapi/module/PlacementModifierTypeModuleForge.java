package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of placement modifier types.
 */
public class PlacementModifierTypeModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PlacementModifierTypeModuleForge::registerPlacementModifierTypes);
    }

    private static void registerPlacementModifierTypes(RegisterEvent event) {
        event.register(Registry.PLACEMENT_MODIFIER_REGISTRY, helper -> AutoRegistrationManager.PLACEMENT_MODIFIER_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerPlacementModifierType(data, helper)));
    }

    private static void registerPlacementModifierType(RegisterData data, RegisterEvent.RegisterHelper<PlacementModifierType<?>> helper) {
        PlacementModifierType<?> placementModifierType = (PlacementModifierType<?>) data.object();
        helper.register(data.name(), placementModifierType);
        data.markProcessed();
    }
}