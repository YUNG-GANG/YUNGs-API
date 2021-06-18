package com.yungnickyoung.minecraft.yungsapi.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

import java.util.function.Supplier;

/**
 * Config for using {@link YungJigsawManager}.
 * Identical in function to vanilla's {@link net.minecraft.world.gen.feature.structure.VillageConfig}.
 */
public class YungJigsawConfig implements IFeatureConfig {
    public static final Codec<YungJigsawConfig> CODEC = RecordCodecBuilder.create((codecBuilder) -> codecBuilder
        .group(
            JigsawPattern.field_244392_b_.fieldOf("start_pool").forGetter(YungJigsawConfig::getStartPoolSupplier),
            Codec.intRange(0, 7).fieldOf("size").forGetter(YungJigsawConfig::getMaxChainPieceLength))
        .apply(codecBuilder, YungJigsawConfig::new));

    /**
     * Supplies the start pool JigsawPattern.
     * Often retrieved in the following manner during structure start generation:
     * {@code () -> dynamicRegistryManager.getRegistry(Registry.JIGSAW_POOL_KEY).getOrDefault(startPoolResourceLocation)}
     */
    private final Supplier<JigsawPattern> startPoolSupplier;

    /**
     * The size of the structure.
     * This is the max distance in Jigsaw pieces from the starting piece that pieces will be placed
     * before terminators are used.
     */
    private final int size;

    public YungJigsawConfig(Supplier<JigsawPattern> startPoolSupplier, int size) {
        this.startPoolSupplier = startPoolSupplier;
        this.size = size;
    }

    public int getMaxChainPieceLength() {
        return this.size;
    }

    public Supplier<JigsawPattern> getStartPoolSupplier() {
        return this.startPoolSupplier;
    }
}

