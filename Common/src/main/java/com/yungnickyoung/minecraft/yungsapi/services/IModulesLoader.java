package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;

public interface IModulesLoader {
    default void loadModules() {
        AutoRegistrationManager.init();
    }
}
