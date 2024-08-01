package com.yungnickyoung.minecraft.yungsapi.world.structure.context;

import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.PieceEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

/**
 * Context class capable of holding various arguments used for jigsaw structure generation and modification.
 * A Builder class is provided for instantiation since various arguments will likely not be needed
 * when using this class.
 */
public class StructureContext {
    private final int pieceMinY;
    private final int pieceMaxY;
    private final int depth;
    private final BlockPos pos;
    private final Rotation rotation;
    private final StructureTemplateManager structureTemplateManager;
    private final List<PieceEntry> pieces;
    private final PieceEntry pieceEntry;
    private final RandomSource random;
    private final RandomState randomState;
    private final BiomeSource biomeSource;

    private StructureContext(StructureContext.Builder builder) {
        this.pieceMinY = builder.pieceMinY;
        this.pieceMaxY = builder.pieceMaxY;
        this.depth = builder.depth;
        this.pos = builder.pos;
        this.structureTemplateManager = builder.structureTemplateManager;
        this.pieces = builder.pieces;
        this.pieceEntry = builder.pieceEntry;
        this.rotation = builder.rotation;
        this.random = builder.random;
        this.randomState = builder.randomState;
        this.biomeSource = builder.biomeSource;
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

    public BlockPos pos() {
        return pos;
    }

    public Rotation rotation() {
        return rotation;
    }

    public StructureTemplateManager structureTemplateManager() {
        return structureTemplateManager;
    }

    public List<PieceEntry> pieces() {
        return pieces;
    }

    public PieceEntry pieceEntry() {
        return this.pieceEntry;
    }

    public RandomSource random() {
        return this.random;
    }

    public RandomState randomState() {
        return this.randomState;
    }

    public BiomeSource biomeSource() {
        return this.biomeSource;
    }

    public static class Builder {
        private int pieceMinY = 0;
        private int pieceMaxY = 0;
        private int depth = 0;
        private BlockPos pos = null;
        private Rotation rotation = null;
        private StructureTemplateManager structureTemplateManager = null;
        private List<PieceEntry> pieces = null;
        private PieceEntry pieceEntry = null;
        private RandomSource random = null;
        private RandomState randomState = null;
        private BiomeSource biomeSource = null;

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

        public Builder pos(BlockPos pos) {
            this.pos = pos;
            return this;
        }

        public Builder rotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder structureTemplateManager(StructureTemplateManager structureTemplateManager) {
            this.structureTemplateManager = structureTemplateManager;
            return this;
        }

        public Builder pieces(List<PieceEntry> pieces) {
            this.pieces = pieces;
            return this;
        }

        public Builder pieceEntry(PieceEntry pieceEntry) {
            this.pieceEntry = pieceEntry;
            return this;
        }

        public Builder random(RandomSource random) {
            this.random = random;
            return this;
        }

        public Builder randomState(RandomState randomState) {
            this.randomState = randomState;
            return this;
        }

        public Builder biomeSource(BiomeSource biomeSource) {
            this.biomeSource = biomeSource;
            return this;
        }

        public StructureContext build() {
            return new StructureContext(this);
        }
    }
}