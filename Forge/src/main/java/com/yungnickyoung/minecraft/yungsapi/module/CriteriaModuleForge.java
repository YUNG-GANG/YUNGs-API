package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiForge;
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
        YungsApiForge.loadingContextEventBus.addListener(YungsApiForge.buildSimpleRegistrar(Registries.TRIGGER_TYPE, AutoRegistrationManager.CRITERION_TRIGGERS));
    }
}