package com.yungnickyoung.minecraft.yungsapi.world.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePlacementTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.structure.exclusion.EnhancedExclusionZone;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;
import java.util.function.Function;

public class EnhancedRandomSpread extends RandomSpreadStructurePlacement {
    public static final Codec<EnhancedRandomSpread> CODEC = RecordCodecBuilder.<EnhancedRandomSpread>mapCodec(builder -> builder
            .group(
                    Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(placement -> placement.locateOffset()),
                    FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(placement -> placement.frequencyReductionMethod()),
                    Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(placement -> placement.frequency()),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(placement -> placement.salt()),
                    ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(placement -> placement.exclusionZone()),
                    EnhancedExclusionZone.CODEC.optionalFieldOf("enhanced_exclusion_zone").forGetter(placement -> placement.enhancedExclusionZone),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("spacing").forGetter(placement -> placement.spacing()),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("separation").forGetter(placement -> placement.separation()),
                    RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(placement -> placement.spreadType()))
            .apply(builder, builder.stable(EnhancedRandomSpread::new)))
            .flatXmap(verifySpacing(), DataResult::success)
            .codec();

    private static Function<EnhancedRandomSpread, DataResult<EnhancedRandomSpread>> verifySpacing() {
        return placement -> placement.spacing() <= placement.separation()
                ? DataResult.error("EnhancedRandomSpread's spacing has to be larger than separation")
                : DataResult.success(placement);
    }

    private final Optional<EnhancedExclusionZone> enhancedExclusionZone;

    public EnhancedRandomSpread(Vec3i locateOffset,
                                       FrequencyReductionMethod frequencyReductionMethod,
                                       Float frequency,
                                       Integer salt,
                                       Optional<ExclusionZone> exclusionZone,
                                       Optional<EnhancedExclusionZone> enhancedExclusionZone,
                                       Integer spacing,
                                       Integer separation,
                                       RandomSpreadType randomSpreadType) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, randomSpreadType);
        this.enhancedExclusionZone = enhancedExclusionZone;
    }

    @Override
    public StructurePlacementType<?> type() {
        return StructurePlacementTypeModule.ENHANCED_RANDOM_SPREAD;
    }

    @Override
    public boolean isPlacementChunk(ChunkGeneratorStructureState chunkGeneratorStructureState, int x, int z) {
        if (!super.isStructureChunk(chunkGeneratorStructureState, x, z)) {
            return false;
        }
        return this.enhancedExclusionZone.isEmpty()
                || !this.enhancedExclusionZone.get().isPlacementForbidden(chunkGeneratorStructureState, x, z);
    }
}
