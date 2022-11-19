package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.world.structure.placement.EnhancedRandomSpread;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

@AutoRegister(YungsApiCommon.MOD_ID)
public class StructurePlacementTypeModule {
    @AutoRegister("enhanced_random_spread")
    public static final StructurePlacementType<EnhancedRandomSpread> ENHANCED_RANDOM_SPREAD =
            () -> EnhancedRandomSpread.CODEC;
}
