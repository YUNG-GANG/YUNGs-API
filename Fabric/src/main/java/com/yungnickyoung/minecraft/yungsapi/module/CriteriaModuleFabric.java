package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.CriteriaTriggersAccessor;
import net.minecraft.advancements.CriterionTrigger;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class CriteriaModuleFabric {
    public static void init() {
        AutoRegistrationManager.CRITERION_TRIGGERS.forEach(CriteriaModuleFabric::register);
    }

    private static void register(RegisterData data) {
        CriteriaTriggersAccessor.getValues().put(data.name(), (CriterionTrigger<?>) data.object());
    }
}