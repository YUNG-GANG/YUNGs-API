package com.yungnickyoung.minecraft.yungsapi.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

import java.util.function.Supplier;

/**
 * Config for using {@link YungJigsawManager}.
 * Identical in function to vanilla's {@link net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration}.
 */
public class YungJigsawConfig implements FeatureConfiguration {
    public static final Codec<YungJigsawConfig> CODEC = RecordCodecBuilder.create((codecBuilder) -> codecBuilder
        .group(
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(YungJigsawConfig::getStartPoolSupplier),
            Codec.intRange(0, 7).fieldOf("size").forGetter(YungJigsawConfig::getMaxChainPieceLength))
        .apply(codecBuilder, YungJigsawConfig::new));

    /**
     * Supplies the start pool StructureTemplatePool.
     * Often retrieved in the following manner during structure start generation:
     * {@code () -> dynamicRegistryManager.getRegistry(Registry.TEMPLATE_POOL_REGISTRY).getOrDefault(startPoolResourceLocation)}
     */
    private final Supplier<StructureTemplatePool> startPoolSupplier;

    /**
     * The size of the structure.
     * This is the max distance in Jigsaw pieces from the starting piece that pieces will be placed
     * before terminators are used.
     */
    private final int size;

    public YungJigsawConfig(Supplier<StructureTemplatePool> startPoolSupplier, int size) {
        this.startPoolSupplier = startPoolSupplier;
        this.size = size;
    }

    public int getMaxChainPieceLength() {
        return this.size;
    }

    public Supplier<StructureTemplatePool> getStartPoolSupplier() {
        return this.startPoolSupplier;
    }
}

