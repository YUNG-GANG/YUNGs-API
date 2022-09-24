package com.yungnickyoung.minecraft.yungsapi.world.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * Compound condition that only passes if all member conditions pass.
 */
public class AllOfCondition extends StructureCondition {
    public static final Codec<AllOfCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    StructureConditionType.CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(condition -> condition.conditions)
            ).apply(builder, AllOfCondition::new));

    /**
     * List of member conditions.
     */
    private final List<StructureCondition> conditions;

    public AllOfCondition(List<StructureCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.ALL_OF;
    }

    @Override
    public boolean passes(ConditionContext ctx) {
        return conditions.stream().allMatch(condition -> condition.passes(ctx));
    }
}
