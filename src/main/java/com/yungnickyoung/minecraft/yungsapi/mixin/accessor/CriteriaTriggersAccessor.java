package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersAccessor {
    @Accessor("CRITERIA")
    static Map<ResourceLocation, CriterionTrigger<?>> getValues() {
        throw new AssertionError();
    }
}