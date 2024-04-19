package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class CriteriaModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(CriteriaModuleNeoForge::registerTriggers);
    }

    private static void registerTriggers(final RegisterEvent event) {
        event.register(Registries.TRIGGER_TYPE, helper -> AutoRegistrationManager.CRITERION_TRIGGERS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerTrigger(data, helper)));
    }

    private static void registerTrigger(AutoRegisterField data, RegisterEvent.RegisterHelper<CriterionTrigger<?>> helper) {
        CriterionTrigger<?> trigger = (CriterionTrigger<?>) data.object();
        helper.register(data.name(), trigger);
        data.markProcessed();
    }
}