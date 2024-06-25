package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;

/**
 * Condition that always passes.
 */
public class AlwaysTrueCondition extends StructureCondition {
    private static final AlwaysTrueCondition INSTANCE = new AlwaysTrueCondition();
    public static final MapCodec<AlwaysTrueCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    public AlwaysTrueCondition() {
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.ALWAYS_TRUE;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        return true;
    }
}
