package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.*;

public class ForgeModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        IModulesLoader.super.loadModules(); // Load common modules
    }

    @Override
    public void processAllModuleEntries() {
        StructurePieceTypeModuleForge.processEntries();
        StructurePoolElementTypeModuleForge.processEntries();
        CriteriaModuleForge.processEntries();
        StructureTypeModuleForge.processEntries();
        FeatureModuleForge.processEntries();
        PlacementModifierTypeModuleForge.processEntries();
        CreativeModeTabModuleForge.processEntries();
        ItemModuleForge.processEntries();
        BlockModuleForge.processEntries();
        BlockEntityTypeModuleForge.processEntries();
        StructureProcessorTypeModuleForge.processEntries();
        StructurePlacementTypeModuleForge.processEntries();
    }
}
