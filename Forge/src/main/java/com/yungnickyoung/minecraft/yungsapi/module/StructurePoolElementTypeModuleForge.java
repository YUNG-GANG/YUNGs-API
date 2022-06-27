package com.yungnickyoung.minecraft.yungsapi.module;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.Map;

public class StructurePoolElementTypeModuleForge {
    public static Map<ResourceLocation, StructurePoolElementType<? extends StructurePoolElement>> STRUCTURE_POOL_ELEMENT_TYPES = new HashMap<>();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(StructurePoolElementTypeModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> STRUCTURE_POOL_ELEMENT_TYPES.forEach((StructurePoolElementTypeModuleForge::register)));
    }

    private static <P extends StructurePoolElement> void register(ResourceLocation resourceLocation, StructurePoolElementType<P> structurePoolElementType) {
        Registry.register(Registry.STRUCTURE_POOL_ELEMENT, resourceLocation, structurePoolElementType);
    }
}
