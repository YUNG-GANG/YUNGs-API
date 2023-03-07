package com.yungnickyoung.minecraft.yungsapi.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.EnhancedTerrainAdaptationType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Config for using {@link YungJigsawManager}.
 */
public class YungJigsawConfig implements FeatureConfiguration {
    public static final Codec<YungJigsawConfig> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    ResourceLocation.CODEC.fieldOf("start_pool").forGetter(YungJigsawConfig::getStartPool),
                    Codec.INT.fieldOf("size").forGetter(YungJigsawConfig::getMaxDepth),
                    Codec.intRange(0, 100).optionalFieldOf("structure_set_avoid_radius_check", 0).forGetter(config -> config.structureAvoidRadius),
                    ResourceKey.codec(Registry.STRUCTURE_SET_REGISTRY).listOf().optionalFieldOf("structure_set_avoid", new ArrayList<>()).forGetter(config -> config.structureSetAvoid),
                    Codec.INT.optionalFieldOf("max_y").forGetter(structure -> structure.maxY),
                    Codec.INT.optionalFieldOf("min_y").forGetter(structure -> structure.minY),
                    EnhancedTerrainAdaptationType.ADAPTATION_CODEC.optionalFieldOf("enhanced_terrain_adaptation", EnhancedTerrainAdaptation.NONE).forGetter(structure -> structure.enhancedTerrainAdaptation))
            .apply(builder, YungJigsawConfig::new));

    private final ResourceLocation startPool;
    private final int structureAvoidRadius;
    private final List<ResourceKey<StructureSet>> structureSetAvoid;
    private final Optional<Integer> maxY;
    private final Optional<Integer> minY;
    private final EnhancedTerrainAdaptation enhancedTerrainAdaptation;

    /**
     * The size of the structure.
     * This is the max distance in Jigsaw pieces from the starting piece that pieces will be placed
     * before terminators are used.
     */
    private final int maxDepth;

    public YungJigsawConfig(
            ResourceLocation startPool,
            int maxDepth,
            int structureAvoidRadius,
            List<ResourceKey<StructureSet>> structureSetAvoid,
            Optional<Integer> maxY,
            Optional<Integer> minY,
            EnhancedTerrainAdaptation enhancedTerrainAdaptation
    ) {
        this.startPool = startPool;
        this.maxDepth = maxDepth;
        this.structureAvoidRadius = structureAvoidRadius;
        this.structureSetAvoid = structureSetAvoid;
        this.maxY = maxY;
        this.minY = minY;
        this.enhancedTerrainAdaptation = enhancedTerrainAdaptation;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public ResourceLocation getStartPool() {
        return this.startPool;
    }

    public int getStructureAvoidRadius() {
        return this.structureAvoidRadius;
    }

    public List<ResourceKey<StructureSet>> getStructureSetAvoid() {
        return this.structureSetAvoid;
    }

    public Optional<Integer> getMaxY() {
        return this.maxY;
    }

    public Optional<Integer> getMinY() {
        return this.minY;
    }

    public EnhancedTerrainAdaptation getEnhancedTerrainAdaptation() {
        return this.enhancedTerrainAdaptation;
    }
}
