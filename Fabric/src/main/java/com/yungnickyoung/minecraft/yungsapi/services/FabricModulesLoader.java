package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.CriteriaModuleFabric;
import com.yungnickyoung.minecraft.yungsapi.module.JigsawModuleFabric;

public class FabricModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        JigsawModuleFabric.init();
        CriteriaModuleFabric.init();
    }
}
