package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of placement modifier types.
 */
public class PlacementModifierTypeModuleForge {
    public static void processEntries() {
        YungsApiForge.loadingContextEventBus.addListener(YungsApiForge.buildSimpleRegistrar(Registries.PLACEMENT_MODIFIER_TYPE, AutoRegistrationManager.PLACEMENT_MODIFIER_TYPES));
    }
}