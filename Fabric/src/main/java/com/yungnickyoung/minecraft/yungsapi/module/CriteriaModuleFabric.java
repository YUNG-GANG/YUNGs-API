package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.CriteriaTriggersAccessor;
import net.minecraft.advancements.CriterionTrigger;

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
        CriteriaTriggersAccessor.getValues().put(data.name(), (CriterionTrigger<?>) data.object());
        data.markProcessed();
    }
}