package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.services.Services;

public class YungAutoRegister {
    public static void scanPackageForAnnotations(String packageName) {
        Services.AUTO_REGISTER.processAllAutoRegEntriesForPackage(packageName);
    }
}