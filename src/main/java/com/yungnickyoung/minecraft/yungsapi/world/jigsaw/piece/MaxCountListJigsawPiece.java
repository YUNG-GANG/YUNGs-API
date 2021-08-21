package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;

import java.util.List;
import java.util.stream.Collectors;

public class MaxCountListJigsawPiece extends ListPoolElement implements IMaxCountJigsawPiece {
    public static final Codec<MaxCountListJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter((listPiece) -> listPiece.elements),
            method_28883(),
            Codec.STRING.fieldOf("name").forGetter(MaxCountListJigsawPiece::getName),
            Codec.INT.fieldOf("max_count").forGetter(MaxCountListJigsawPiece::getMaxCount))
        .apply(builder, MaxCountListJigsawPiece::new));

    protected final int maxCount;
    protected final String name;

    public MaxCountListJigsawPiece(List<StructurePoolElement> elements, StructurePool.Projection projection, String name, int maxCount) {
        super(elements, projection);
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
        return YAModJigsaw.MAX_COUNT_LIST_ELEMENT;
    }

    public String toString() {
        return "MaxCountList[" + this.name + "][" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "][" + this.maxCount + "]";
    }
}
