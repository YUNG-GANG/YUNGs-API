package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

/**
 * Condition that randomly passes with the specified chance.
 */
public class RandomChanceCondition extends StructureCondition {
    public static final Codec<RandomChanceCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("chance").forGetter(condition -> condition.chance))
            .apply(builder, RandomChanceCondition::new));

    public final float chance;

    public RandomChanceCondition(float chance) {
        this.chance = chance;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.RANDOM_CHANCE;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        // Extract args from context
        RandomSource random = ctx.random();

        // Abort if missing any args
        if (random == null) {
            YungsApiCommon.LOGGER.error("Missing required field 'random' for random_chance condition!");
            return false;
        }

        return random.nextFloat() < this.chance;
    }
}
