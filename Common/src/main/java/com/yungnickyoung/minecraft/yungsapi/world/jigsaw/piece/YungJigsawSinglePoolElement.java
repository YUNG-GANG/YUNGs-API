package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class YungJigsawSinglePoolElement extends SinglePoolElement {
    public static final Codec<YungJigsawSinglePoolElement> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    templateCodec(),
                    processorsCodec(),
                    projectionCodec(),
                    Codec.STRING.optionalFieldOf("name").forGetter(element -> element.name),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_count").forGetter(element -> element.maxCount),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_required_depth").forGetter(element -> element.minRequiredDepth),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_possible_depth").forGetter(element -> element.maxPossibleDepth),
                    Codec.BOOL.optionalFieldOf("is_priority", false).forGetter(element -> element.isPriority),
                    Codec.BOOL.optionalFieldOf("ignore_bounds", false).forGetter(element -> element.ignoreBounds)
            ).apply(builder, YungJigsawSinglePoolElement::new));

    public final Optional<Integer> maxCount;
    public final Optional<String> name;
    public final Optional<Integer> minRequiredDepth;
    public final Optional<Integer> maxPossibleDepth;
    public final boolean isPriority;
    public final boolean ignoreBounds;

    public YungJigsawSinglePoolElement(
            Either<ResourceLocation, StructureTemplate> resourceLocation,
            Holder<StructureProcessorList> processors,
            StructureTemplatePool.Projection projection,
            Optional<String> name,
            Optional<Integer> maxCount,
            Optional<Integer> minRequiredDepth,
            Optional<Integer> maxPossibleDepth,
            boolean isPriority,
            boolean ignoreBounds
    ) {
        super(resourceLocation, processors, projection);
        this.maxCount = maxCount;
        this.name = name;
        this.minRequiredDepth = minRequiredDepth;
        this.maxPossibleDepth = maxPossibleDepth;
        this.isPriority = isPriority;
        this.ignoreBounds = ignoreBounds;
    }

    public StructurePoolElementType<?> getType() {
        return StructurePoolElementTypeModule.YUNG_SINGLE_ELEMENT;
    }

    public boolean isPriorityPiece() {
        return isPriority;
    }

    public boolean ignoresBounds() {
        return this.ignoreBounds;
    }

    public boolean isAtValidDepth(int depth) {
        boolean isAtMinRequiredDepth = minRequiredDepth.isEmpty() || minRequiredDepth.get() <= depth;
        boolean isAtMaxAllowableDepth = maxPossibleDepth.isEmpty() || maxPossibleDepth.get() >= depth;
        return isAtMinRequiredDepth && isAtMaxAllowableDepth;
    }

    public String toString() {
        return String.format("YungJigsawSingle[%s][%s][%s][%s][%s][%s]",
                this.name,
                this.template,
                this.maxCount.isPresent() ? maxCount.get() : "N/A",
                this.minRequiredDepth.isPresent() ? "" + minRequiredDepth.get() : "N/A",
                this.maxPossibleDepth.isPresent() ? "" + maxPossibleDepth.get() : "N/A",
                String.valueOf(this.isPriority));
    }
}
