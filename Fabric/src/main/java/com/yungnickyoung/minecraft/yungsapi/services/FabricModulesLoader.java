package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.*;

public class FabricModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        IModulesLoader.super.loadModules(); // Load common modules
    }

    @Override
    public void processAllModuleEntries() {
        processModuleEntries();
    }

    /**
     * Duplicate static method so that entry processing can be called by other mods.
     * A corresponding static method does not exist in ForgeModulesLoader because the AutoReg
     * system for Forge does not actually use the package name passed in for AutoReg.
     */
    public static void processModuleEntries() {
        StructurePieceTypeModuleFabric.processEntries();
        StructurePoolElementTypeModuleFabric.processEntries();
        CriteriaModuleFabric.processEntries();
        StructureTypeModuleFabric.processEntries();
        CreativeModeTabModuleFabric.processEntries();
        ItemModuleFabric.processEntries();
        BlockModuleFabric.processEntries();
        BlockEntityTypeModuleFabric.processEntries();
        StructureProcessorTypeModuleFabric.processEntries();
    }
}
