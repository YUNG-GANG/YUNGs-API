package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of custom Jigsaw pieces.
 * For more information, read about {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager}
 */
public class StructurePoolElementTypeModuleForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(StructurePoolElementTypeModuleForge::commonSetup);
    }

    private static void commonSetup(final RegisterEvent event) {
        event.register(Registries.STRUCTURE_POOL_ELEMENT, helper -> AutoRegistrationManager.STRUCTURE_POOL_ELEMENT_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> register(data, helper)));
    }

    private static void register(AutoRegisterField data, RegisterEvent.RegisterHelper<StructurePoolElementType<?>> helper) {
        helper.register(data.name(),  (StructurePoolElementType<?>) data.object());
        data.markProcessed();
    }
}