package com.yungnickyoung.minecraft.yungsapi.module;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.world.structure.placement.EnhancedRandomSpread;

@AutoRegister(YungsApiCommon.MOD_ID)
public class StructurePlacementTypeModule {
    @AutoRegister("enhanced_random_spread")
    public static final MapCodec<EnhancedRandomSpread> ENHANCED_RANDOM_SPREAD = EnhancedRandomSpread.CODEC;
}
