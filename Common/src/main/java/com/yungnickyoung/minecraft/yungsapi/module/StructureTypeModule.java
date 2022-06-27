package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class StructureTypeModule {
    public static StructureType<YungJigsawStructure> YUNG_JIGSAW = () -> YungJigsawStructure.CODEC;

    public static void init() {
        Services.REGISTRY.registerStructureType(new ResourceLocation(YungsApiCommon.MOD_ID, "yung_jigsaw"), YUNG_JIGSAW);
    }
}
