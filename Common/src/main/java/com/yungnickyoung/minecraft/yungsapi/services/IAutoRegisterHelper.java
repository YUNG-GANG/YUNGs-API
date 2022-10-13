package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;

import java.util.List;

public interface IAutoRegisterHelper {
    List<RegisterData> getAllAutoRegisterFieldsInPackage(String packageName);
    void autoRegisterAllObjects(List<RegisterData> allRegisterData);
    void processAllAutoRegEntriesForPackage(String packageName);
}