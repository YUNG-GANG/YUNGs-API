package com.yungnickyoung.minecraft.yungsapi.world.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;

/**
 * Simple condition that passes if its wrapped condition does not pass.
 */
public class NotCondition extends StructureCondition {
    public static final Codec<NotCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    StructureConditionType.CONDITION_CODEC.fieldOf("condition").forGetter(condition -> condition.condition)
            ).apply(builder, NotCondition::new));

    /**
     * Wrapped condition to be negated.
     */
    private final StructureCondition condition;

    public NotCondition(StructureCondition condition) {
        this.condition = condition;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.NOT;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        return !condition.passes(ctx);
    }
}
