package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public void registerStructureType(ResourceLocation resourceLocation, StructureType<? extends Structure> structureType) {
        Registry.register(Registry.STRUCTURE_TYPES, resourceLocation, structureType);
    }

    @Override
    public void registerStructurePoolElementType(ResourceLocation resourceLocation, StructurePoolElementType<? extends StructurePoolElement> structurePoolElementType) {
        Registry.register(Registry.STRUCTURE_POOL_ELEMENT, resourceLocation, structurePoolElementType);
    }
}
