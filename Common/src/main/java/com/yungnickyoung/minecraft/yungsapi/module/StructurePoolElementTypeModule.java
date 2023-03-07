package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountFeaturePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountLegacySinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountListPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountSinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.YungJigsawSinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

@AutoRegister(YungsApiCommon.MOD_ID)
public class StructurePoolElementTypeModule {
    @AutoRegister("max_count_single_element")
    public static StructurePoolElementType<MaxCountSinglePoolElement> MAX_COUNT_SINGLE_ELEMENT =
            () -> MaxCountSinglePoolElement.CODEC;

    @AutoRegister("max_count_legacy_single_element")
    public static StructurePoolElementType<MaxCountLegacySinglePoolElement> MAX_COUNT_LEGACY_SINGLE_ELEMENT =
            () -> MaxCountLegacySinglePoolElement.CODEC;

    @AutoRegister("max_count_feature_element")
    public static StructurePoolElementType<MaxCountFeaturePoolElement> MAX_COUNT_FEATURE_ELEMENT =
            () -> MaxCountFeaturePoolElement.CODEC;

    @AutoRegister("max_count_list_element")
    public static StructurePoolElementType<MaxCountListPoolElement> MAX_COUNT_LIST_ELEMENT =
            () -> MaxCountListPoolElement.CODEC;

    @AutoRegister("yung_single_element")
    public static StructurePoolElementType<YungJigsawSinglePoolElement> YUNG_SINGLE_ELEMENT =
            () -> YungJigsawSinglePoolElement.CODEC;
}
