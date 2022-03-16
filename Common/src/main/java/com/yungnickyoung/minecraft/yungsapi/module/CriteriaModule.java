package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.criteria.SafeStructureLocationTrigger;
import net.minecraft.resources.ResourceLocation;

public class CriteriaModule {
    public static final SafeStructureLocationTrigger SAFE_STRUCTURE_LOCATION = new SafeStructureLocationTrigger(new ResourceLocation(YungsApiCommon.MOD_ID, "structure_location"));
}
