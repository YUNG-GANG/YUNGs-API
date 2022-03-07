package com.yungnickyoung.minecraft.yungsapi.init;

import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import com.yungnickyoung.minecraft.yungsapi.criteria.SafeStructureLocationTrigger;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.CriteriaTriggersAccessor;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class YAModCriteria {
    public static final SafeStructureLocationTrigger SAFE_STRUCTURE_LOCATION = new SafeStructureLocationTrigger(new ResourceLocation(YungsApi.MOD_ID, "structure_location"));

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(YAModCriteria::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Map<ResourceLocation, CriterionTrigger<?>> values = CriteriaTriggersAccessor.getValues();
            values.put(SAFE_STRUCTURE_LOCATION.getId(), SAFE_STRUCTURE_LOCATION);
        });
    }
}