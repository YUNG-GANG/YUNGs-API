package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.*;

public class FabricModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        IModulesLoader.super.loadModules(); // Load common modules
        StructurePoolElementTypeModuleFabric.init();
        CriteriaModuleFabric.init();
        StructureTypeModuleFabric.init();
        CreativeModeTabModuleFabric.init();
        ItemModuleFabric.init();
        BlockModuleFabric.init();
        StructureProcessorTypeModuleFabric.init();
        BlockEntityTypeModuleFabric.init();
    }
}
