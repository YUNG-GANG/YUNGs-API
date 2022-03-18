package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

/**
 * Registration of custom Jigsaw pieces.
 * For more information, read about {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager}
 */
public class JigsawModuleFabric {
    public static void init() {
        register("max_count_single_element", JigsawModule.MAX_COUNT_SINGLE_ELEMENT);
        register("max_count_legacy_single_element", JigsawModule.MAX_COUNT_LEGACY_SINGLE_ELEMENT);
        register("max_count_feature_element", JigsawModule.MAX_COUNT_FEATURE_ELEMENT);
        register("max_count_list_element", JigsawModule.MAX_COUNT_LIST_ELEMENT);
    }

    private static <P extends StructurePoolElement> StructurePoolElementType<P> register(String name, StructurePoolElementType<P> structurePoolElementType) {
        return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, new ResourceLocation(YungsApiCommon.MOD_ID, name), structurePoolElementType);
    }
}
