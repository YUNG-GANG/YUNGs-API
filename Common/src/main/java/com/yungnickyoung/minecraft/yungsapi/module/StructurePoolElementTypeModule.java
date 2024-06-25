package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.MaxCountListPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawFeatureElement;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawSinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

@AutoRegister(YungsApiCommon.MOD_ID)
public class StructurePoolElementTypeModule {
    @AutoRegister("max_count_list_element")
    public static StructurePoolElementType<MaxCountListPoolElement> MAX_COUNT_LIST_ELEMENT =
            () -> MaxCountListPoolElement.CODEC;
    @AutoRegister("yung_single_element")
    public static StructurePoolElementType<YungJigsawSinglePoolElement> YUNG_SINGLE_ELEMENT =
            () -> YungJigsawSinglePoolElement.CODEC;

    @AutoRegister("yung_feature_element")
    public static StructurePoolElementType<YungJigsawFeatureElement> YUNG_FEATURE_ELEMENT =
            () -> YungJigsawFeatureElement.CODEC;
}
