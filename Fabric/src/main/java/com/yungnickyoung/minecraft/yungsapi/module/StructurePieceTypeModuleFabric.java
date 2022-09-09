package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

/**
 * Registration of StructurePieceTypes.
 */
public class StructurePieceTypeModuleFabric {
    public static void init() {
        AutoRegistrationManager.STRUCTURE_PIECE_TYPES.forEach(StructurePieceTypeModuleFabric::register);
    }

    private static void register(RegisterData data) {
        Registry.register(Registry.STRUCTURE_PIECE, data.name(), (StructurePieceType) data.object());
    }
}