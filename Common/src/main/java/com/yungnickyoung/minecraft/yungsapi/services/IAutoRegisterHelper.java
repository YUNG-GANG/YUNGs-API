package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;

import java.util.List;

public interface IAutoRegisterHelper {
    List<RegisterData> getAllAutoRegisterFields();
    void autoRegisterAllObjects(List<RegisterData> allRegisterData);
}
