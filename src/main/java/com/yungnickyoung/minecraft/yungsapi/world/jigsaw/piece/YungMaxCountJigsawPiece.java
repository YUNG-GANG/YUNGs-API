package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class YungMaxCountJigsawPiece extends YungSingleJigsawPiece implements IYungJigsawPiece {
    public static final Codec<YungMaxCountJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            func_236846_c_(),
            func_236844_b_(),
            func_236848_d_(),
            Codec.STRING.fieldOf("name").forGetter(YungSingleJigsawPiece::getName),
            Codec.INT.fieldOf("max_count").forGetter(YungMaxCountJigsawPiece::getMaxCount))
        .apply(builder, YungMaxCountJigsawPiece::new));

    private final int maxCount;

    public YungMaxCountJigsawPiece(Either<ResourceLocation, Template> resourceLocation, Supplier<StructureProcessorList> processors, JigsawPattern.PlacementBehaviour projection, String name, int maxCount) {
        super(resourceLocation, processors, projection, name);
        this.maxCount = maxCount;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public IJigsawDeserializer<?> getType() {
        return YAModJigsaw.YUNG_MAX_COUNT_ELEMENT;
    }

    public String toString() {
        return "YungMaxCountSingle[" + this.name + "][" + this.field_236839_c_ + "][" + this.maxCount + "]";
    }
}

