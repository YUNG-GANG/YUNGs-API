package com.yungnickyoung.minecraft.yungsapi.world.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * Compound condition that passes if any of its member conditions pass.
 */
public class AnyOfCondition extends StructureCondition {
    public static final Codec<AnyOfCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    StructureConditionType.CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(condition -> condition.conditions)
            ).apply(builder, AnyOfCondition::new));

    /**
     * List of member conditions.
     */
    private final List<StructureCondition> conditions;

    public AnyOfCondition(List<StructureCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.ANY_OF;
    }

    @Override
    public boolean passes(ConditionContext ctx) {
        return conditions.stream().anyMatch(condition -> condition.passes(ctx));
    }
}
