package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of custom Jigsaw pieces.
 * For more information, read about {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager}
 */
public class StructurePoolElementTypeModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(StructurePoolElementTypeModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.STRUCTURE_POOL_ELEMENT_TYPES.forEach(StructurePoolElementTypeModuleForge::register));
    }

    private static void register(RegisterData data) {
        Registry.register(Registry.STRUCTURE_POOL_ELEMENT, data.name(),  (StructurePoolElementType<?>) data.object());
    }
}