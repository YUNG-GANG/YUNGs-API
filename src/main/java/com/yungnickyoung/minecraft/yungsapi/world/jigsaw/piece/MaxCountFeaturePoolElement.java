package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.FeaturePoolElementAccessor;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PlacedFeatureAccessor;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.structures.FeaturePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Supplier;

public class MaxCountFeaturePoolElement extends FeaturePoolElement implements IMaxCountJigsawPiece {
    public static final Codec<MaxCountFeaturePoolElement> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            PlacedFeature.CODEC.fieldOf("feature").forGetter((featurePoolElement) -> ((FeaturePoolElementAccessor)featurePoolElement).getFeature()),
            projectionCodec(),
            Codec.STRING.fieldOf("name").forGetter(MaxCountFeaturePoolElement::getName),
            Codec.INT.fieldOf("max_count").forGetter(MaxCountFeaturePoolElement::getMaxCount))
        .apply(builder, MaxCountFeaturePoolElement::new));

    protected final int maxCount;
    protected final String name;

    public MaxCountFeaturePoolElement(Supplier<PlacedFeature> feature, StructureTemplatePool.Projection projection, String name, int maxCount) {
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
        return "MaxCountFeature[" + this.name + "][" + Registry.FEATURE.getKey((Feature<?>) ((PlacedFeatureAccessor)(((FeaturePoolElementAccessor)this).getFeature().get())).getFeature()) + "][" + this.maxCount + "]";
    }
}
