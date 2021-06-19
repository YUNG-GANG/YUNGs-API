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
public class MaxCountFeatureJigsawPiece extends FeatureJigsawPiece implements IMaxCountJigsawPiece {
    public static final Codec<MaxCountFeatureJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            ConfiguredFeature.field_236264_b_.fieldOf("feature").forGetter((featurePiece) -> featurePiece.configuredFeature),
            func_236848_d_(),
            Codec.STRING.fieldOf("name").forGetter(MaxCountFeatureJigsawPiece::getName),
            Codec.INT.fieldOf("max_count").forGetter(MaxCountFeatureJigsawPiece::getMaxCount))
        .apply(builder, MaxCountFeatureJigsawPiece::new));

    protected final int maxCount;
    protected String name;

    public MaxCountFeatureJigsawPiece(Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier, JigsawPattern.PlacementBehaviour projection, String name, int maxCount) {
        super(configuredFeatureSupplier, projection);
        this.name = name;
        this.maxCount = maxCount;
    }

    @Override
    public int getMaxCount() {
        return this.maxCount;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public IJigsawDeserializer<?> getType() {
        return YAModJigsaw.MAX_COUNT_FEATURE_ELEMENT;
    }

    public String toString() {
        return "MaxCountFeature[" + this.name + "][" + ForgeRegistries.FEATURES.getKey(this.configuredFeature.get().getFeature()) + "][" + this.maxCount + "]";
    }
}
