package com.yungnickyoung.minecraft.yungsapi.world.jigsaw;

import com.yungnickyoung.minecraft.yungsapi.util.BoxOctree;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PieceEntry {
    private final PoolElementStructurePiece piece;
    private final MutableObject<BoxOctree> boxOctree;
    private final int depth;
    private final List<PieceEntry> childEntries = new ArrayList<>();
    private final PieceEntry parentEntry;
    private final JigsawManager.PieceContext sourcePieceContext;

    public PieceEntry(PoolElementStructurePiece piece, MutableObject<BoxOctree> boxOctree, int depth, PieceEntry parentEntry, JigsawManager.PieceContext sourcePieceContext) {
        this.piece = piece;
        this.boxOctree = boxOctree;
        this.depth = depth;
        this.parentEntry = parentEntry;
        this.sourcePieceContext = sourcePieceContext;
    }

    public void addChildEntry(PieceEntry childEntry) {
        this.childEntries.add(childEntry);
    }

    public boolean hasChildren() {
        return this.childEntries.size() > 0;
    }

    public PoolElementStructurePiece getPiece() {
        return piece;
    }

    public MutableObject<BoxOctree> getBoxOctree() {
        return boxOctree;
    }

    public int getDepth() {
        return depth;
    }

    public PieceEntry getParentEntry() {
        return parentEntry;
    }

    public JigsawManager.PieceContext getSourcePieceContext() {
        return sourcePieceContext;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PieceEntry) obj;
        return Objects.equals(this.piece, that.piece) &&
                Objects.equals(this.boxOctree, that.boxOctree) &&
                this.depth == that.depth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, boxOctree, depth);
    }

    @Override
    public String toString() {
        return "PieceEntry[" +
                "piece=" + piece + ", " +
                "boxOctreeMutableObject=" + boxOctree + ", " +
                "depth=" + depth + ']';
    }
}
