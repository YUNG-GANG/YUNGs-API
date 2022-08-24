package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

/**
 * Registration of StructureProcessorTypes.
 */
public class StructureProcessorTypeModuleFabric {
    public static void init() {
        AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES.forEach(StructureProcessorTypeModuleFabric::register);
    }

    private static void register(RegisterData data) {
        Registry.register(Registry.STRUCTURE_PROCESSOR, data.name(),  (StructureProcessorType<?>) data.object());
    }
}