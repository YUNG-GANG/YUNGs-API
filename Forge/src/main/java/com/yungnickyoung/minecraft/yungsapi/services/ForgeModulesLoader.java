package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.CriteriaModuleForge;
import com.yungnickyoung.minecraft.yungsapi.module.JigsawModuleForge;

public class ForgeModulesLoader implements IModulesLoader {
    @Override
    public void loadModules() {
        JigsawModuleForge.init();
        CriteriaModuleForge.init();
    }
}
