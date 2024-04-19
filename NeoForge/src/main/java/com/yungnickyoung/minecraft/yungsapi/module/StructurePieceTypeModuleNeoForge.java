package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of StructurePieceTypes.
 */
public class StructurePieceTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(StructurePieceTypeModuleNeoForge::commonSetup);
    }

    private static void commonSetup(final RegisterEvent event) {
        event.register(Registries.STRUCTURE_PIECE, helper -> AutoRegistrationManager.STRUCTURE_PIECE_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerPieceType(data, helper)));
    }

    private static void registerPieceType(AutoRegisterField data, RegisterEvent.RegisterHelper<StructurePieceType> helper) {
        helper.register(data.name(), (StructurePieceType) data.object());
        data.markProcessed();
    }
}