package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.services.Services;

import java.util.ArrayList;
import java.util.List;

public class AutoRegistrationManager {
    public static final List<AutoRegisterField> STRUCTURE_FEATURES = new ArrayList<>();
    public static final List<AutoRegisterField> FEATURES = new ArrayList<>();
    public static final List<AutoRegisterField> CONFIGURED_FEATURES = new ArrayList<>();
    public static final List<AutoRegisterField> PLACED_FEATURES = new ArrayList<>();
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
    public static final List<AutoRegisterField> COMMANDS = new ArrayList<>();

    /**
     * Scans all {@link AutoRegister} annotated fields and prepares them for registration, independent of mod loader.
     * Subsequently scans all {@link AutoRegister} annotated methods and invokes them.
     *
     * @param packageName Name of a package containing {@link AutoRegister} annotated fields and/or methods.
     *                    When specifying a package, try to be as precise as possible,
     *                    as all subpackages will also be recursively scanned.
     */
    public static void initAutoRegPackage(String packageName) {
        // Scan package for AutoRegister annotated fields.
        // References to each of these fields will be stored, so we can register them for the appropriate loader.
        Services.AUTO_REGISTER.collectAllAutoRegisterFieldsInPackage(packageName);

        // In Fabric, AutoRegister fields are registered.
        // In Forge, AutoRegister events for handling registration are subscribed to.
        Services.AUTO_REGISTER.processQueuedAutoRegEntries();

        // AutoRegister methods are invoked
        Services.AUTO_REGISTER.invokeAllAutoRegisterMethods(packageName);
    }
}
