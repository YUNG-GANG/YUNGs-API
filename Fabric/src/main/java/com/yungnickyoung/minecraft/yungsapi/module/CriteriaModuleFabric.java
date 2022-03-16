package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.CriteriaTriggersAccessor;
import com.yungnickyoung.minecraft.yungsapi.module.CriteriaModule;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class CriteriaModuleFabric {
    public static void init() {
        Map<ResourceLocation, CriterionTrigger<?>> values = CriteriaTriggersAccessor.getValues();
        values.put(CriteriaModule.SAFE_STRUCTURE_LOCATION.getId(), CriteriaModule.SAFE_STRUCTURE_LOCATION);
    }
}