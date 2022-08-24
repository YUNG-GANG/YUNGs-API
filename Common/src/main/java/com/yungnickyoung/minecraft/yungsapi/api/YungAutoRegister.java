package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;

public class YungAutoRegister {
    public static void registerMod(Class<?> clazz) {
        YungsApiCommon.registeredModPackages.add(clazz.getPackageName());
    }
}