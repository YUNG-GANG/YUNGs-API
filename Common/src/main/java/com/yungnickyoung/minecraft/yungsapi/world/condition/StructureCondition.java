package com.yungnickyoung.minecraft.yungsapi.world.condition;

/**
 * A serializable class used for checking specific parameters during world generation.
 * Can be used for jigsaw pieces as well as processors.
 */
public abstract class StructureCondition {
    public static final StructureCondition ALWAYS_TRUE = new AlwaysTrueCondition();
    abstract public StructureConditionType<?> type();
    abstract public boolean passes(ConditionContext ctx);
}
