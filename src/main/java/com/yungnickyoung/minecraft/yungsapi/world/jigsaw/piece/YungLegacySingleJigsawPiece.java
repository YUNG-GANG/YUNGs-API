package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.init.YAModJigsaw;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.LegacySingleJigsawPiece;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class YungLegacySingleJigsawPiece extends LegacySingleJigsawPiece implements IYungJigsawPiece {
    public static final Codec<YungLegacySingleJigsawPiece> CODEC = RecordCodecBuilder.create((builder) -> builder
        .group(
            func_236846_c_(),
            func_236844_b_(),
            func_236848_d_(),
            Codec.STRING.fieldOf("name").forGetter(YungLegacySingleJigsawPiece::getName))
        .apply(builder, YungLegacySingleJigsawPiece::new));

    protected String name;

    public YungLegacySingleJigsawPiece(Either<ResourceLocation, Template> resourceLocation, Supplier<StructureProcessorList> processors, JigsawPattern.PlacementBehaviour projection, String name) {
        super(resourceLocation, processors, projection);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public IJigsawDeserializer<?> getType() {
        return YAModJigsaw.YUNG_LEGACY_SINGLE_ELEMENT;
    }

    public String toString() {
        return "YungLegacySingle[" + this.name + "][" + this.field_236839_c_ + "]";
    }
}
