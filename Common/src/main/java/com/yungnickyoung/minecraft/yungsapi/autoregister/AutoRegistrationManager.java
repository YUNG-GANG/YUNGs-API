package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.services.Services;

import java.util.ArrayList;
import java.util.List;

public class AutoRegistrationManager {
    public static final List<AutoRegisterField> STRUCTURE_FEATURES = new ArrayList<>();
    public static final List<AutoRegisterField> FEATURES = new ArrayList<>();
    public static final List<AutoRegisterField> STRUCTURE_PROCESSOR_TYPES = new ArrayList<>();
    public static final List<AutoRegisterField> STRUCTURE_POOL_ELEMENT_TYPES = new ArrayList<>();
    public static final List<AutoRegisterField> CRITERION_TRIGGERS = new ArrayList<>();
    public static final List<AutoRegisterField> BLOCKS = new ArrayList<>();
    public static final List<AutoRegisterField> ITEMS = new ArrayList<>();
    public static final List<AutoRegisterField> BLOCK_ENTITY_TYPES = new ArrayList<>();
    public static final List<AutoRegisterField> CREATIVE_MODE_TABS = new ArrayList<>();
    public static final List<AutoRegisterField> BIOMES = new ArrayList<>();
    public static final List<AutoRegisterField> ENTITY_TYPES = new ArrayList<>();
    public static final List<AutoRegisterField> MOB_EFFECTS = new ArrayList<>();
    public static final List<AutoRegisterField> POTIONS = new ArrayList<>();
    public static final List<AutoRegisterField> PARTICLE_TYPES = new ArrayList<>();
    public static final List<AutoRegisterField> SOUND_EVENTS = new ArrayList<>();

    public static void preLoad() {
        // Scan all mod files to check for AutoRegister annotated field.
        // Then grab references to each of those fields, so we can register them for the appropriate loader.
        Services.AUTO_REGISTER.prepareAllAutoRegisterFields();
    }

    public static void postLoad() {
        // Invoke all AutoRegister annotated methods.
        // These invocations are done in post-load because often times they rely on objects to be properly registered first.
        Services.AUTO_REGISTER.invokeAllAutoRegisterMethods();
    }
}
