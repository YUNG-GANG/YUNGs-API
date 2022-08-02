package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IModulesLoader MODULES = load(IModulesLoader.class);
    public static final IAutoRegisterHelper AUTO_REGISTER = load(IAutoRegisterHelper.class);
    public static final ICreativeModeTabProvider CREATIVE_MODE_TAB = load(ICreativeModeTabProvider.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        YungsApiCommon.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
