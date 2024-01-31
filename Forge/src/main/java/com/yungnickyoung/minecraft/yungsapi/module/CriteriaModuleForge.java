package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class CriteriaModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CriteriaModuleForge::registerTriggers);
    }

    private static void registerTriggers(RegisterEvent event) {
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