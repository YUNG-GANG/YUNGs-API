package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;

/**
 * Registration of structure types.
 */
public class StructureTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.STRUCTURE_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(StructureTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        Registry.register(BuiltInRegistries.STRUCTURE_TYPE, data.name(), (StructureType<?>) data.object());
        data.markProcessed();
    }
}