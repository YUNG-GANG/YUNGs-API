package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.CriteriaModuleForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModuleForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructureTypeModuleForge;

public class ForgeModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        IModulesLoader.super.loadModules(); // Load common modules
        CriteriaModuleForge.init();
        StructureTypeModuleForge.init();
        StructurePoolElementTypeModuleForge.init();
    }
}
