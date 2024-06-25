package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

/**
 * Condition for constraining placement based on jigsaw piece depth.
 */
public class DepthCondition extends StructureCondition {
    public static final MapCodec<DepthCondition> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder
            .group(
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_required_depth").forGetter(condition -> condition.minRequiredDepth),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_possible_depth").forGetter(condition -> condition.maxPossibleDepth))
            .apply(builder, DepthCondition::new));

    /**
     * The minimum required depth (in jigsaw pieces) from the structure start
     * at which this piece is allowed to spawn (inclusive).
     */
    public final Optional<Integer> minRequiredDepth;

    /**
     * The maximum allowed depth (in jigsaw pieces) from the structure start
     * at which this piece is allowed to spawn (inclusive).
     */
    public final Optional<Integer> maxPossibleDepth;

    public DepthCondition(Optional<Integer> minRequiredDepth, Optional<Integer> maxPossibleDepth) {
        this.minRequiredDepth = minRequiredDepth;
        this.maxPossibleDepth = maxPossibleDepth;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.DEPTH;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        int depth = ctx.depth();
        boolean isAtMinRequiredDepth = minRequiredDepth.isEmpty() || minRequiredDepth.get() <= depth;
        boolean isAtMaxAllowableDepth = maxPossibleDepth.isEmpty() || maxPossibleDepth.get() >= depth;
        return isAtMinRequiredDepth && isAtMaxAllowableDepth;
    }
}
