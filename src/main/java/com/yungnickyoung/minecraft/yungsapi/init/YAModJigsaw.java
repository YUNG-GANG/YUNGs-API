package com.yungnickyoung.minecraft.yungsapi.init;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountFeaturePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountLegacySinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountListPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountSinglePoolElement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;

/**
 * Registration of custom Jigsaw pieces.
 * For more information, read about {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager}
 */
public class YAModJigsaw {
    public static StructurePoolElementType<MaxCountSinglePoolElement> MAX_COUNT_SINGLE_ELEMENT =
        register("max_count_single_element", MaxCountSinglePoolElement.CODEC);

    public static StructurePoolElementType<MaxCountLegacySinglePoolElement> MAX_COUNT_LEGACY_SINGLE_ELEMENT =
        register("max_count_legacy_single_element", MaxCountLegacySinglePoolElement.CODEC);

    public static StructurePoolElementType<MaxCountFeaturePoolElement> MAX_COUNT_FEATURE_ELEMENT =
        register("max_count_feature_element", MaxCountFeaturePoolElement.CODEC);

    public static StructurePoolElementType<MaxCountListPoolElement> MAX_COUNT_LIST_ELEMENT =
        register("max_count_list_element", MaxCountListPoolElement.CODEC);

    public static void init() {
        // static bootstrap
    }

    private static <P extends StructurePoolElement> StructurePoolElementType<P> register(String name, Codec<P> codec) {
        return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, new ResourceLocation("yungsapi", name), () -> codec);
    }
}