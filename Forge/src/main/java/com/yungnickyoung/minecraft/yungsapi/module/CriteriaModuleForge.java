package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.CriteriaTriggersAccessor;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class CriteriaModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CriteriaModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AutoRegistrationManager.CRITERION_TRIGGERS.forEach(CriteriaModuleForge::register));
    }

    private static void register(AutoRegisterField data) {
        CriteriaTriggersAccessor.getValues().put(data.name(), (CriterionTrigger<?>) data.object());
    }
}