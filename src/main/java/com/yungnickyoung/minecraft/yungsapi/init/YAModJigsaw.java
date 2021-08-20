package com.yungnickyoung.minecraft.yungsapi.init;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountFeatureJigsawPiece;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountLegacySingleJigsawPiece;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountListJigsawPiece;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.MaxCountSingleJigsawPiece;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Registration of custom Jigsaw pieces.
 * For more information, read about {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager}
 */
public class YAModJigsaw {
    public static StructurePoolElementType<MaxCountSingleJigsawPiece> MAX_COUNT_SINGLE_ELEMENT =
        register("max_count_single_element", MaxCountSingleJigsawPiece.CODEC);

    public static StructurePoolElementType<MaxCountLegacySingleJigsawPiece> MAX_COUNT_LEGACY_SINGLE_ELEMENT =
        register("max_count_legacy_single_element", MaxCountLegacySingleJigsawPiece.CODEC);

    public static StructurePoolElementType<MaxCountFeatureJigsawPiece> MAX_COUNT_FEATURE_ELEMENT =
        register("max_count_feature_element", MaxCountFeatureJigsawPiece.CODEC);

    public static StructurePoolElementType<MaxCountListJigsawPiece> MAX_COUNT_LIST_ELEMENT =
        register("max_count_list_element", MaxCountListJigsawPiece.CODEC);

    public static void init() {
        // static bootstrap
    }

    private static <P extends StructurePoolElement> StructurePoolElementType<P> register(String name, Codec<P> codec) {
        return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, new Identifier("yungsapi", name), () -> codec);
    }
}
