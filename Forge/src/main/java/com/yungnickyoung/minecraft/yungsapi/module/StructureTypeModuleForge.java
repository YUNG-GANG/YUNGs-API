package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of structure types.
 */
public class StructureTypeModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(StructureTypeModuleForge::registerStructureTypes);
    }

    private static void registerStructureTypes(RegisterEvent event) {
        event.register(Registry.STRUCTURE_TYPE_REGISTRY, helper -> {
            AutoRegistrationManager.STRUCTURE_TYPES.forEach(data -> {
                StructureType<?> structureType = (StructureType<?>) data.object();
                helper.register(data.name(), structureType);
            });
        });
    }
}