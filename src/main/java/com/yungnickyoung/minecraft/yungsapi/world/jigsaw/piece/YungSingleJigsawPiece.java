package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class YungSingleJigsawPiece extends SingleJigsawPiece implements IYungJigsawPiece {
    public static final Codec<YungSingleJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            func_236846_c_(),
            func_236844_b_(),
            func_236848_d_(),
            Codec.STRING.fieldOf("name").forGetter(YungSingleJigsawPiece::getName))
        .apply(builder, YungSingleJigsawPiece::new));

    protected String name;

    public YungSingleJigsawPiece(Either<ResourceLocation, Template> resourceLocation, Supplier<StructureProcessorList> processors, JigsawPattern.PlacementBehaviour projection, String name) {
        super(resourceLocation, processors, projection);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public IJigsawDeserializer<?> getType() {
        return YAModJigsaw.YUNG_SINGLE_ELEMENT;
    }

    public String toString() {
        return "YungSingle[" + this.name + "][" + this.field_236839_c_ + "]";
    }
}