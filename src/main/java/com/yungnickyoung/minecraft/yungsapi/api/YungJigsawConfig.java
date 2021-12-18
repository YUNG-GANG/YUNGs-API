package com.yungnickyoung.minecraft.yungsapi.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Config for using {@link YungJigsawManager}.
 * Nearly identical in function to vanilla's {@link net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration},
 * with the one difference being that the start pool variable is a resource location pointing to the initial template pool,
 * rather than a supplier.
 */
public class YungJigsawConfig implements FeatureConfiguration {
    public static final Codec<YungJigsawConfig> CODEC = RecordCodecBuilder.create((codecBuilder) -> codecBuilder
        .group(
            ResourceLocation.CODEC.fieldOf("start_pool").forGetter(YungJigsawConfig::getStartPool),
            Codec.INT.fieldOf("size").forGetter(YungJigsawConfig::getMaxDepth))
        .apply(codecBuilder, YungJigsawConfig::new));

    private final ResourceLocation startPool;

    /**
     * The size of the structure.
     * This is the max distance in Jigsaw pieces from the starting piece that pieces will be placed
     * before terminators are used.
     */
    private final int maxDepth;

    public YungJigsawConfig(ResourceLocation startPool, int maxDepth) {
        this.startPool = startPool;
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public ResourceLocation getStartPool() {
        return this.startPool;
    }
}
