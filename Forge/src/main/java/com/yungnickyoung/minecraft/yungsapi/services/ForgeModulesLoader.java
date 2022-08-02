package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.*;

public class ForgeModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        IModulesLoader.super.loadModules(); // Load common modules
        StructurePoolElementTypeModuleForge.init();
        CriteriaModuleForge.init();
        StructureFeatureModuleForge.init();
        ItemModuleForge.init();
        BlockModuleForge.init();
        StructureProcessorTypeModuleForge.init();
    }
}
