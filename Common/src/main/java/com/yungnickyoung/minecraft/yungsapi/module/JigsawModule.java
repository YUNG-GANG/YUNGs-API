package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountFeaturePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountLegacySinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountListPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountSinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class JigsawModule {
    public static StructurePoolElementType<MaxCountSinglePoolElement> MAX_COUNT_SINGLE_ELEMENT =
            () -> MaxCountSinglePoolElement.CODEC;
    public static StructurePoolElementType<MaxCountLegacySinglePoolElement> MAX_COUNT_LEGACY_SINGLE_ELEMENT =
            () -> MaxCountLegacySinglePoolElement.CODEC;
    public static StructurePoolElementType<MaxCountFeaturePoolElement> MAX_COUNT_FEATURE_ELEMENT =
            () -> MaxCountFeaturePoolElement.CODEC;
    public static StructurePoolElementType<MaxCountListPoolElement> MAX_COUNT_LIST_ELEMENT =
            () -> MaxCountListPoolElement.CODEC;
}
