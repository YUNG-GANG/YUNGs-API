package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of StructurePieceTypes.
 */
public class StructurePieceTypeModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(StructurePieceTypeModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.STRUCTURE_PIECE_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(StructurePieceTypeModuleForge::register));
    }

    private static void register(AutoRegisterField data) {
        Registry.register(BuiltInRegistries.STRUCTURE_PIECE, data.name(),  (StructurePieceType) data.object());
        data.markProcessed();
    }
}