package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModuleForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructureTypeModuleForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class ForgeRegistryHelper implements IRegistryHelper {
    @Override
    public void registerStructureType(ResourceLocation resourceLocation, StructureType<? extends Structure> structureType) {
        StructureTypeModuleForge.STRUCTURE_TYPES.put(resourceLocation, structureType);
    }

    @Override
    public void registerStructurePoolElementType(ResourceLocation resourceLocation, StructurePoolElementType<? extends StructurePoolElement> structurePoolElementType) {
        StructurePoolElementTypeModuleForge.STRUCTURE_POOL_ELEMENT_TYPES.put(resourceLocation, structurePoolElementType);
    }
}
