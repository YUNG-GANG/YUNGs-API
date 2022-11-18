package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public class CustomAdaptation extends EnhancedTerrainAdaptation {
    public static final Codec<CustomAdaptation> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    Codec.BOOL.optionalFieldOf("carves", true).forGetter(conditon -> conditon.doCarving),
                    Codec.BOOL.optionalFieldOf("beards", true).forGetter(conditon -> conditon.doBearding),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("kernel_size").forGetter(conditon -> conditon.kernelSize),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("kernel_distance").forGetter(conditon -> conditon.kernelDistance))
            .apply(builder, CustomAdaptation::new));

    CustomAdaptation(boolean doCarving, boolean doBearding, int kernelSize, int kernelDistance) {
        super(kernelSize, kernelDistance, doCarving, doBearding);
    }

    @Override
    public EnhancedTerrainAdaptationType<?> type() {
        return EnhancedTerrainAdaptationType.CUSTOM;
    }
}
