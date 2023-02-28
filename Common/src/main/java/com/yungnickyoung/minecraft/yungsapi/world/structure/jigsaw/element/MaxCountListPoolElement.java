package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.ListPoolElementAccessor;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Equivalent of vanilla {@link ListPoolElement} with additional support for max_count setting.
 * Prefer using {@link YungJigsawSinglePoolElement} if possible instead.
 */
public class MaxCountListPoolElement extends ListPoolElement implements IMaxCountJigsawPoolElement {
    public static final Codec<MaxCountListPoolElement> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter((listPiece) -> ((ListPoolElementAccessor)listPiece).getElements()),
            projectionCodec(),
            Codec.STRING.fieldOf("name").forGetter(MaxCountListPoolElement::getName),
            Codec.INT.fieldOf("max_count").forGetter(MaxCountListPoolElement::getMaxCount))
        .apply(builder, MaxCountListPoolElement::new));

    protected final int maxCount;
    protected final String name;

    public MaxCountListPoolElement(List<StructurePoolElement> elements, StructureTemplatePool.Projection projection, String name, int maxCount) {
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
        return StructurePoolElementTypeModule.MAX_COUNT_LIST_ELEMENT;
    }

    public String toString() {
        return "MaxCountList[" + this.name + "][" + ((ListPoolElementAccessor)this).getElements().stream().map(Object::toString).collect(Collectors.joining(", ")) + "][" + this.maxCount + "]";
    }
}
