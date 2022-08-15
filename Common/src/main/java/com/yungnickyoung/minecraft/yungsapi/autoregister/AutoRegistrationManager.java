package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.services.Services;

import java.util.ArrayList;
import java.util.List;

public class AutoRegistrationManager {
    public static final List<RegisterData> STRUCTURE_FEATURES = new ArrayList<>();
    public static final List<RegisterData> STRUCTURE_PROCESSOR_TYPES = new ArrayList<>();
    public static final List<RegisterData> STRUCTURE_POOL_ELEMENT_TYPES = new ArrayList<>();
    public static final List<RegisterData> CRITERION_TRIGGERS = new ArrayList<>();
    public static final List<RegisterData> BLOCKS = new ArrayList<>();
    public static final List<RegisterData> ITEMS = new ArrayList<>();
    public static final List<RegisterData> BLOCK_ENTITY_TYPES = new ArrayList<>();
    public static final List<RegisterData> CREATIVE_MODE_TABS = new ArrayList<>();

    public static void init() {
        // Scan all mod files to check for AutoRegister annotated fields.
        // Grab references to each of those fields so we can register them for the appropriate loader.
        List<RegisterData> allAutoRegisterData = Services.AUTO_REGISTER.getAllAutoRegisterFields();
        Services.AUTO_REGISTER.autoRegisterAllObjects(allAutoRegisterData);
    }
}
