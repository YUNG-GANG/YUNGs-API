package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureType;

@AutoRegister(YungsApiCommon.MOD_ID)
public class StructureTypeModule {
    @AutoRegister("yung_jigsaw")
    public static StructureType<YungJigsawStructure> YUNG_JIGSAW = () -> YungJigsawStructure.CODEC;
}
