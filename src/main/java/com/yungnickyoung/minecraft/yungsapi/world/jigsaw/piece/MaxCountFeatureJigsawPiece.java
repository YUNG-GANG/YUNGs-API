package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.function.Supplier;

public class MaxCountFeatureJigsawPiece extends FeaturePoolElement implements IMaxCountJigsawPiece {
    public static final Codec<MaxCountFeatureJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature").forGetter((featurePiece) -> featurePiece.feature),
            method_28883(),
            Codec.STRING.fieldOf("name").forGetter(MaxCountFeatureJigsawPiece::getName),
            Codec.INT.fieldOf("max_count").forGetter(MaxCountFeatureJigsawPiece::getMaxCount))
        .apply(builder, MaxCountFeatureJigsawPiece::new));

    protected final int maxCount;
    protected final String name;

    public MaxCountFeatureJigsawPiece(Supplier<ConfiguredFeature<?, ?>> feature, StructurePool.Projection projection, String name, int maxCount) {
        super(feature, projection);
        this.maxCount = maxCount;
        this.name = name;
    }

    @Override
    public int getMaxCount() {
        return this.maxCount;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public StructurePoolElementType<?> getType() {
        return YAModJigsaw.MAX_COUNT_FEATURE_ELEMENT;
    }

    public String toString() {
        return "MaxCountFeature[" + this.name + "][" + Registry.FEATURE.getId((this.feature.get()).getFeature()) + "][" + this.maxCount + "]";
    }
}
