package com.yungnickyoung.minecraft.yungsapi.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool;

import java.util.function.Supplier;

/**
 * Config for using {@link YungJigsawManager}.
 * Identical in function to vanilla's {@link net.minecraft.world.gen.feature.StructurePoolFeatureConfig}.
 */
public class YungJigsawConfig {
    public static final Codec<YungJigsawConfig> CODEC = RecordCodecBuilder.create((codecBuilder) -> codecBuilder
        .group(
            StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(YungJigsawConfig::getStartPoolSupplier),
            Codec.intRange(0, 7).fieldOf("size").forGetter(YungJigsawConfig::getMaxChainPieceLength))
        .apply(codecBuilder, YungJigsawConfig::new));

    /**
     * Supplies the start pool StructurePool.
     * Often retrieved in the following manner during structure start generation:
     * {@code () -> dynamicRegistryManager.get(Registry.TEMPLATE_POOL_WORLDGEN).get(startPoolResourceLocation)}
     */
    private final Supplier<StructurePool> startPoolSupplier;

    /**
     * The size of the structure.
     * This is the max distance in Jigsaw pieces from the starting piece that pieces will be placed
     * before terminators are used.
     */
    private final int size;

    public YungJigsawConfig(Supplier<StructurePool> startPoolSupplier, int size) {
        this.startPoolSupplier = startPoolSupplier;
        this.size = size;
    }

    public int getMaxChainPieceLength() {
        return this.size;
    }

    public Supplier<StructurePool> getStartPoolSupplier() {
        return this.startPoolSupplier;
    }
}
