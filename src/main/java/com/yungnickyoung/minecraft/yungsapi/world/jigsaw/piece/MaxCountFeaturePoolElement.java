package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.FeaturePoolElementAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

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

    public MaxCountFeaturePoolElement(Holder<PlacedFeature> feature, StructureTemplatePool.Projection projection, String name, int maxCount) {
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

    public @NotNull StructurePoolElementType<?> getType() {
        return YAModJigsaw.MAX_COUNT_FEATURE_ELEMENT;
    }

    public @NotNull String toString() {
        return "MaxCountFeature[" + this.name + "][" + Registry.FEATURE.getKey((((FeaturePoolElementAccessor)this).getFeature().value()).feature().value().feature()) + "][" + this.maxCount + "]";
    }
}
