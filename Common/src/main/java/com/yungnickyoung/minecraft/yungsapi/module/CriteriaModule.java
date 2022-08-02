package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.criteria.SafeStructureLocationTrigger;
import net.minecraft.resources.ResourceLocation;

@AutoRegister(YungsApiCommon.MOD_ID)
public class CriteriaModule {
    @AutoRegister("structure_location")
    public static final SafeStructureLocationTrigger SAFE_STRUCTURE_LOCATION =
            new SafeStructureLocationTrigger(new ResourceLocation(YungsApiCommon.MOD_ID, "structure_location"));
}
