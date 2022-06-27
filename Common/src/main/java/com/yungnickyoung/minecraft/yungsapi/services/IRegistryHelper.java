package com.yungnickyoung.minecraft.yungsapi.services;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public interface IRegistryHelper {
    void registerStructureType(ResourceLocation resourceLocation, StructureType<? extends Structure> structureType);

    void registerStructurePoolElementType(ResourceLocation resourceLocation, StructurePoolElementType<? extends StructurePoolElement> structurePoolElementType);
}
