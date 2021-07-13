package com.yungnickyoung.minecraft.yungsapi.init;

import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import com.yungnickyoung.minecraft.yungsapi.criteria.SafeStructurePositionCriterion;
import com.yungnickyoung.minecraft.yungsapi.mixin.CriteriaAccessor;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class YAModCriteria {
    public static final SafeStructurePositionCriterion SAFE_STRUCTURE_LOCATION = new SafeStructurePositionCriterion(new Identifier(YungsApi.MOD_ID, "structure_location"));

    public static void init() {
        Map<Identifier, Criterion<?>> values = CriteriaAccessor.getValues();
        values.put(SAFE_STRUCTURE_LOCATION.getId(), SAFE_STRUCTURE_LOCATION);
    }
}
