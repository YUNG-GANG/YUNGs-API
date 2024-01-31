package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class CriteriaModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.CRITERION_TRIGGERS.stream()
                .filter(data -> !data.processed())
                .forEach(CriteriaModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        CriterionTrigger<?> trigger = (CriterionTrigger<?>) data.object();
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, data.name(), trigger);
        data.markProcessed();
    }
}