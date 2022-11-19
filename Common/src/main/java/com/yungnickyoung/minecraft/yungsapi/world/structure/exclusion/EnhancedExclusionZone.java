package com.yungnickyoung.minecraft.yungsapi.world.structure.exclusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.ExclusionZone;

/**
 * Enhanced version of vanilla's {@link ExclusionZone} that allows for specifying multiple structure sets to avoid.
 */
public class EnhancedExclusionZone {
    public static final Codec<EnhancedExclusionZone> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    RegistryCodecs.homogeneousList(Registry.STRUCTURE_SET_REGISTRY, StructureSet.DIRECT_CODEC)
                            .fieldOf("other_set")
                            .forGetter(zone -> zone.otherSet),
                    Codec.intRange(1, 16)
                            .fieldOf("chunk_count")
                            .forGetter(zone -> zone.chunkCount))
            .apply(builder, EnhancedExclusionZone::new));

    private final HolderSet<StructureSet> otherSet;
    private final int chunkCount;

    public EnhancedExclusionZone(HolderSet<StructureSet> otherSet, int chunkCount) {
        this.otherSet = otherSet;
        this.chunkCount = chunkCount;
    }

    public boolean isPlacementForbidden(ChunkGenerator chunkGenerator, RandomState randomState, long seed, int x, int z) {
        for (Holder<StructureSet> holder : this.otherSet) {
            if (chunkGenerator.hasStructureChunkInRange(holder, randomState, seed, x, z, this.chunkCount)) {
                return true;
            }
        }

        return false;
    }
}
