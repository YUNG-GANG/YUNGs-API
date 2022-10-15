package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

/**
 * Registration of custom Jigsaw pieces.
 * For more information, read about {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager}
 */
public class StructurePoolElementTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.STRUCTURE_POOL_ELEMENT_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(StructurePoolElementTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        Registry.register(Registry.STRUCTURE_POOL_ELEMENT, data.name(), (StructurePoolElementType<?>) data.object());
        data.markProcessed();
    }
}