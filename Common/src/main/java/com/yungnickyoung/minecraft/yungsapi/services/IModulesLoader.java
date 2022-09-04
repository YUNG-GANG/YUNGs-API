package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;

public interface IModulesLoader {
    default void loadModules() {
        // AutoRegister fields are queued for registration
        AutoRegistrationManager.preLoad();

        // In Fabric, AutoRegister fields are registered.
        // In Forge, AutoRegister events for handling registration are subscribed to.
        loadModLoaderDependentModules();

        // AutoRegister methods are invoked
        AutoRegistrationManager.postLoad();
    }

    /**
     * Loader-dependent registration logic.
     */
    void loadModLoaderDependentModules();
}
