package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of StructurePieceTypes.
 */
public class StructurePieceTypeModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(StructurePieceTypeModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.STRUCTURE_PIECE_TYPES.forEach(StructurePieceTypeModuleForge::register));
    }

    private static void register(RegisterData data) {
        Registry.register(Registry.STRUCTURE_PIECE, data.name(),  (StructurePieceType) data.object());
    }
}