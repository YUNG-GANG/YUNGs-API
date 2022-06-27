package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountFeaturePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountLegacySinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountListPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountSinglePoolElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class StructurePoolElementTypeModule {
    public static StructurePoolElementType<MaxCountSinglePoolElement> MAX_COUNT_SINGLE_ELEMENT =
            () -> MaxCountSinglePoolElement.CODEC;
    public static StructurePoolElementType<MaxCountLegacySinglePoolElement> MAX_COUNT_LEGACY_SINGLE_ELEMENT =
            () -> MaxCountLegacySinglePoolElement.CODEC;
    public static StructurePoolElementType<MaxCountFeaturePoolElement> MAX_COUNT_FEATURE_ELEMENT =
            () -> MaxCountFeaturePoolElement.CODEC;
    public static StructurePoolElementType<MaxCountListPoolElement> MAX_COUNT_LIST_ELEMENT =
            () -> MaxCountListPoolElement.CODEC;

    public static void init () {
        Services.REGISTRY.registerStructurePoolElementType(new ResourceLocation(YungsApiCommon.MOD_ID, "max_count_single_element"), MAX_COUNT_SINGLE_ELEMENT);
        Services.REGISTRY.registerStructurePoolElementType(new ResourceLocation(YungsApiCommon.MOD_ID, "max_count_legacy_single_element"), MAX_COUNT_LEGACY_SINGLE_ELEMENT);
        Services.REGISTRY.registerStructurePoolElementType(new ResourceLocation(YungsApiCommon.MOD_ID, "max_count_feature_element"), MAX_COUNT_FEATURE_ELEMENT);
        Services.REGISTRY.registerStructurePoolElementType(new ResourceLocation(YungsApiCommon.MOD_ID, "max_count_list_element"), MAX_COUNT_LIST_ELEMENT);
    }
}
