package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.CriteriaTriggersAccessor;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class CriteriaModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CriteriaModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Map<ResourceLocation, CriterionTrigger<?>> values = CriteriaTriggersAccessor.getValues();
            values.put(CriteriaModule.SAFE_STRUCTURE_LOCATION.getId(), CriteriaModule.SAFE_STRUCTURE_LOCATION);
        });
    }
}