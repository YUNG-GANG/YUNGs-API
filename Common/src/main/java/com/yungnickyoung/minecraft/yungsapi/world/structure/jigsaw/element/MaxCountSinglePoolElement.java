package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

/**
 * Equivalent of vanilla {@link SinglePoolElement} with additional support for max_count setting.
 * Deprecated. Use {@link YungJigsawSinglePoolElement} instead.
 */
@Deprecated
public class MaxCountSinglePoolElement extends SinglePoolElement implements IMaxCountJigsawPoolElement {
    public static final MapCodec<MaxCountSinglePoolElement> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder
            .group(
                    templateCodec(),
                    processorsCodec(),
                    projectionCodec(),
                    overrideLiquidSettingsCodec(),
                    Codec.STRING.fieldOf("name").forGetter(MaxCountSinglePoolElement::getName),
                    Codec.INT.fieldOf("max_count").forGetter(MaxCountSinglePoolElement::getMaxCount))
            .apply(builder, MaxCountSinglePoolElement::new));

    protected final int maxCount;
    protected final String name;

    public MaxCountSinglePoolElement(Either<ResourceLocation, StructureTemplate> resourceLocation,
                                     Holder<StructureProcessorList> processors,
                                     StructureTemplatePool.Projection projection,
                                     Optional<LiquidSettings> liquidSettings,
                                     String name, int maxCount) {
        super(resourceLocation, processors, projection, liquidSettings);
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
        return StructurePoolElementTypeModule.MAX_COUNT_SINGLE_ELEMENT;
    }

    public String toString() {
        return "MaxCountSingle[" + this.name + "][" + this.template + "][" + this.maxCount + "]";
    }
}