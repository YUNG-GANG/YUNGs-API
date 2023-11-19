package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.criteria.SafeStructureLocationTrigger;

@AutoRegister(YungsApiCommon.MOD_ID)
public class CriteriaModule {
    @AutoRegister("structure_location")
    public static final SafeStructureLocationTrigger SAFE_STRUCTURE_LOCATION = new SafeStructureLocationTrigger();
}
