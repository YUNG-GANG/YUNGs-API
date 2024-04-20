package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;

/**
 * Registration of StructurePieceTypes.
 */
public class StructurePieceTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildSimpleRegistrar(Registries.STRUCTURE_PIECE, AutoRegistrationManager.STRUCTURE_PIECE_TYPES));
    }
}