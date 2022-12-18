package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Equivalent of vanilla {@link LegacySinglePoolElement} with additional support for max_count setting.
 * Prefer using {@link YungJigsawSinglePoolElement} if possible instead.
 */
public class MaxCountLegacySinglePoolElement extends LegacySinglePoolElement implements IMaxCountJigsawPoolElement {
    public static final Codec<MaxCountLegacySinglePoolElement> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            templateCodec(),
            processorsCodec(),
            projectionCodec(),
            Codec.STRING.fieldOf("name").forGetter(MaxCountLegacySinglePoolElement::getName),
            Codec.INT.fieldOf("max_count").forGetter(MaxCountLegacySinglePoolElement::getMaxCount))
        .apply(builder, MaxCountLegacySinglePoolElement::new));

    protected final int maxCount;
    protected final String name;

    public MaxCountLegacySinglePoolElement(Either<ResourceLocation, StructureTemplate> resourceLocation, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection, String name, int maxCount) {
        super(resourceLocation, processors, projection);
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
        return StructurePoolElementTypeModule.MAX_COUNT_LEGACY_SINGLE_ELEMENT;
    }

    public String toString() {
        return "MaxCountLegacySingle[" + this.name + "][" + this.template + "][" + this.maxCount + "]";
    }
}
