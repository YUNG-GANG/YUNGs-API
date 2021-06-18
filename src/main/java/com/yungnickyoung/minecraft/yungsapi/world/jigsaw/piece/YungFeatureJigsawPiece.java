package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.jigsaw.FeatureJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class YungFeatureJigsawPiece extends FeatureJigsawPiece implements IYungJigsawPiece {
    public static final Codec<YungFeatureJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            ConfiguredFeature.field_236264_b_.fieldOf("feature").forGetter((featurePiece) -> featurePiece.configuredFeature),
            func_236848_d_(),
            Codec.STRING.fieldOf("name").forGetter(YungFeatureJigsawPiece::getName))
        .apply(builder, YungFeatureJigsawPiece::new));

    protected String name;

    public YungFeatureJigsawPiece(Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier, JigsawPattern.PlacementBehaviour projection, String name) {
        super(configuredFeatureSupplier, projection);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public IJigsawDeserializer<?> getType() {
        return YAModJigsaw.YUNG_FEATURE_ELEMENT;
    }

    public String toString() {
        return "YungFeature[" + this.name + "][" + ForgeRegistries.FEATURES.getKey(this.configuredFeature.get().getFeature()) + "]";
    }
}
