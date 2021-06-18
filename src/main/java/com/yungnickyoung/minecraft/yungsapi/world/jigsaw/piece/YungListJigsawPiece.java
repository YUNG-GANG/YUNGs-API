package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.ListJigsawPiece;

import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
public class YungListJigsawPiece extends ListJigsawPiece implements IYungJigsawPiece {
    public static final Codec<YungListJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            JigsawPiece.field_236847_e_.listOf().fieldOf("elements").forGetter((listPiece) -> listPiece.elements),
            func_236848_d_(),
            Codec.STRING.fieldOf("name").forGetter(YungListJigsawPiece::getName))
        .apply(builder, YungListJigsawPiece::new));

    protected String name;

    public YungListJigsawPiece(List<JigsawPiece> elements, JigsawPattern.PlacementBehaviour projection, String name) {
        super(elements, projection);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public IJigsawDeserializer<?> getType() {
        return YAModJigsaw.YUNG_LIST_ELEMENT;
    }

    public String toString() {
        return "YungList[" + this.name + "][" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }
}
