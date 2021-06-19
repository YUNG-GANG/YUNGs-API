package com.yungnickyoung.minecraft.yungsapi.init;

import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class YAModJigsaw {
    public static IJigsawDeserializer<MaxCountSingleJigsawPiece> MAX_COUNT_SINGLE_ELEMENT;
    public static IJigsawDeserializer<MaxCountLegacySingleJigsawPiece> MAX_COUNT_LEGACY_SINGLE_ELEMENT;
    public static IJigsawDeserializer<MaxCountFeatureJigsawPiece> MAX_COUNT_FEATURE_ELEMENT;
    public static IJigsawDeserializer<MaxCountListJigsawPiece> MAX_COUNT_LIST_ELEMENT;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(YAModJigsaw::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MAX_COUNT_SINGLE_ELEMENT = Registry.register(
                Registry.STRUCTURE_POOL_ELEMENT,
                new ResourceLocation(YungsApi.MOD_ID, "max_count_single_element"),
                () -> MaxCountSingleJigsawPiece.CODEC);

            MAX_COUNT_LEGACY_SINGLE_ELEMENT = Registry.register(
                Registry.STRUCTURE_POOL_ELEMENT,
                new ResourceLocation(YungsApi.MOD_ID, "max_count_legacy_single_element"),
                () -> MaxCountLegacySingleJigsawPiece.CODEC);

            MAX_COUNT_FEATURE_ELEMENT = Registry.register(
                Registry.STRUCTURE_POOL_ELEMENT,
                new ResourceLocation(YungsApi.MOD_ID, "max_count_feature_element"),
                () -> MaxCountFeatureJigsawPiece.CODEC);

            MAX_COUNT_LIST_ELEMENT = Registry.register(
                Registry.STRUCTURE_POOL_ELEMENT,
                new ResourceLocation(YungsApi.MOD_ID, "max_count_list_element"),
                () -> MaxCountListJigsawPiece.CODEC);
        });
    }
}
