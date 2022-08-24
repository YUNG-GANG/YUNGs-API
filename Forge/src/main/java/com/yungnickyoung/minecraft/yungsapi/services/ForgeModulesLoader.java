package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.*;

public class ForgeModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        IModulesLoader.super.loadModules(); // Load common modules
        StructurePoolElementTypeModuleForge.init();
        CriteriaModuleForge.init();
        StructureTypeModuleForge.init();
        CreativeModeTabModuleForge.init();
        ItemModuleForge.init();
        BlockModuleForge.init();
        BlockEntityTypeModuleForge.init();
        StructureProcessorTypeModuleForge.init();
    }
}
