package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

/**
 * Registration of StructureProcessorTypes.
 */
public class StructureProcessorTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(StructureProcessorTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, data.name(),  (StructureProcessorType<?>) data.object());
        data.markProcessed();
    }
}