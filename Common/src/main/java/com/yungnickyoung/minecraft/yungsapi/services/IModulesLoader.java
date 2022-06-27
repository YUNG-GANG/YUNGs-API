package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import com.yungnickyoung.minecraft.yungsapi.module.StructureTypeModule;

public interface IModulesLoader {
    default void loadModules() {
        StructureTypeModule.init();
        StructurePoolElementTypeModule.init();
    }
}
