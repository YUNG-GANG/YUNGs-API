package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import net.minecraft.structure.Structure;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class MaxCountSingleJigsawPiece extends SinglePoolElement implements IMaxCountJigsawPiece {
    public static final Codec<MaxCountSingleJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            method_28882(),
            method_28880(),
            method_28883(),
            Codec.STRING.fieldOf("name").forGetter(MaxCountSingleJigsawPiece::getName),
            Codec.INT.fieldOf("max_count").forGetter(MaxCountSingleJigsawPiece::getMaxCount))
        .apply(builder, MaxCountSingleJigsawPiece::new));

    protected final int maxCount;
    protected final String name;

    public MaxCountSingleJigsawPiece(Either<Identifier, Structure> resourceLocation, Supplier<StructureProcessorList> processors, StructurePool.Projection projection, String name, int maxCount) {
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
        return YAModJigsaw.MAX_COUNT_SINGLE_ELEMENT;
    }

    public String toString() {
        return "MaxCountSingle[" + this.name + "][" + this.location + "][" + this.maxCount + "]";
    }
}
