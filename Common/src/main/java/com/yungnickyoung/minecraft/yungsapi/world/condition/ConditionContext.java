package com.yungnickyoung.minecraft.yungsapi.world.condition;

/**
 * Holds various arguments used for checking conditions.
 * A Builder class is provided for instantiation since various arguments will likely not be needed
 * when passing information to conditions.
 */
public class ConditionContext {
    private final int pieceMinY;
    private final int pieceMaxY;
    private final int depth;

    private ConditionContext(ConditionContext.Builder builder) {
        this.pieceMinY = builder.pieceMinY;
        this.pieceMaxY = builder.pieceMaxY;
        this.depth = builder.depth;
    }

    public int pieceMinY() {
        return pieceMinY;
    }

    public int pieceMaxY() {
        return pieceMaxY;
    }

    public int depth() {
        return depth;
    }

    public static class Builder {
        private int pieceMinY = 0;
        private int pieceMaxY = 0;
        private int depth = 0;

        public Builder() {
        }

        public Builder pieceMinY(int pieceMinY) {
            this.pieceMinY = pieceMinY;
            return this;
        }

        public Builder pieceMaxY(int pieceMaxY) {
            this.pieceMaxY = pieceMaxY;
            return this;
        }

        public Builder depth(int depth) {
            this.depth = depth;
            return this;
        }

        public ConditionContext build() {
            return new ConditionContext(this);
        }
    }
}