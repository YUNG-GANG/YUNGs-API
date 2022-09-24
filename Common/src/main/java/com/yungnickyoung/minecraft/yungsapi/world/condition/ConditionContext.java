package com.yungnickyoung.minecraft.yungsapi.world.condition;

/**
 * Holds arguments used for checking conditions.
 * It may be viable to pass in dummy values for some arguments, depending on the expected condition(s).
 */
public record ConditionContext(
        int pieceMinY,
        int pieceMaxY
) {
}
