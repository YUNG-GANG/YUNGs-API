package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

/**
 * Registration of StructurePieceTypes.
 */
public class StructurePieceTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.STRUCTURE_PIECE_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(StructurePieceTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        Registry.register(Registry.STRUCTURE_PIECE, data.name(), (StructurePieceType) data.object());
        data.markProcessed();
    }
}