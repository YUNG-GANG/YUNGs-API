//package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;
//
//import com.mojang.datafixers.util.Either;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
//import mcp.MethodsReturnNonnullByDefault;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
//import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
//import net.minecraft.world.gen.feature.jigsaw.LegacySingleJigsawPiece;
//import net.minecraft.world.gen.feature.template.StructureProcessorList;
//import net.minecraft.world.gen.feature.template.Template;
//
//import java.util.function.Supplier;
//
//@MethodsReturnNonnullByDefault
//public class MaxCountLegacySingleJigsawPiece extends LegacySingleJigsawPiece implements IMaxCountJigsawPiece {
//    public static final Codec<MaxCountLegacySingleJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
//        .group(
//            func_236846_c_(),
//            func_236844_b_(),
//            func_236848_d_(),
//            Codec.STRING.fieldOf("name").forGetter(MaxCountLegacySingleJigsawPiece::getName),
//            Codec.INT.fieldOf("max_count").forGetter(MaxCountLegacySingleJigsawPiece::getMaxCount))
//        .apply(builder, MaxCountLegacySingleJigsawPiece::new));
//
//    protected final int maxCount;
//    protected String name;
//
//    public MaxCountLegacySingleJigsawPiece(Either<ResourceLocation, Template> resourceLocation, Supplier<StructureProcessorList> processors, JigsawPattern.PlacementBehaviour projection, String name, int maxCount) {
//        super(resourceLocation, processors, projection);
//        this.maxCount = maxCount;
//        this.name = name;
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
//        return YAModJigsaw.MAX_COUNT_LEGACY_SINGLE_ELEMENT;
//    }
//
//    public String toString() {
//        return "MaxCountLegacySingle[" + this.name + "][" + this.field_236839_c_ + "][" + this.maxCount + "]";
//    }
//}
