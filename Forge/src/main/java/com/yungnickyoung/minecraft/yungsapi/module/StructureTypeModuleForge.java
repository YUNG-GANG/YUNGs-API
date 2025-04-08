package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of structure types.
 */
public class StructureTypeModuleForge {
    public static void processEntries() {
        YungsApiForge.loadingContextEventBus.addListener(YungsApiForge.buildSimpleRegistrar(Registries.STRUCTURE_TYPE, AutoRegistrationManager.STRUCTURE_TYPES));
    }
}