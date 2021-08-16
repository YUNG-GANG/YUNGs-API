//package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
//import mcp.MethodsReturnNonnullByDefault;
//import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
//import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
//import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
//import net.minecraft.world.gen.feature.jigsaw.ListJigsawPiece;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@MethodsReturnNonnullByDefault
//public class MaxCountListJigsawPiece extends ListJigsawPiece implements IMaxCountJigsawPiece {
//    public static final Codec<MaxCountListJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
//        .group(
//            JigsawPiece.field_236847_e_.listOf().fieldOf("elements").forGetter((listPiece) -> listPiece.elements),
//            func_236848_d_(),
//            Codec.STRING.fieldOf("name").forGetter(MaxCountListJigsawPiece::getName),
//            Codec.INT.fieldOf("max_count").forGetter(MaxCountListJigsawPiece::getMaxCount))
//        .apply(builder, MaxCountListJigsawPiece::new));
//
//    protected final int maxCount;
//    protected String name;
//
//    public MaxCountListJigsawPiece(List<JigsawPiece> elements, JigsawPattern.PlacementBehaviour projection, String name, int maxCount) {
//        super(elements, projection);
//        this.name = name;
//        this.maxCount = maxCount;
//    }
//
//    @Override
//    public int getMaxCount() {
//        return this.maxCount;
//    }
//
//    @Override
//    public String getName() {
//        return this.name;
//    }
//
//    public IJigsawDeserializer<?> getType() {
//        return YAModJigsaw.MAX_COUNT_LIST_ELEMENT;
//    }
//
//    public String toString() {
//        return "MaxCountList[" + this.name + "][" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "][" + this.maxCount + "]";
//    }
//}
