package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw;

import com.yungnickyoung.minecraft.yungsapi.util.BoxOctree;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.assembler.PieceContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawSinglePoolElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PieceEntry {
    private PoolElementStructurePiece piece;
    private final MutableObject<BoxOctree> boxOctree;
    private final AABB pieceAabb;
    private final int depth;
    private final List<PieceEntry> childEntries = new ArrayList<>();
    private final PieceEntry parentEntry;
    private final PieceContext sourcePieceContext;
    private final JigsawJunction parentJunction;
    private boolean delayGeneration = false;

    public PieceEntry(PoolElementStructurePiece piece, MutableObject<BoxOctree> boxOctree, AABB pieceAabb, int depth,
                      PieceEntry parentEntry, PieceContext sourcePieceContext, JigsawJunction parentJunction) {
        this.piece = piece;
        this.boxOctree = boxOctree;
        this.pieceAabb = pieceAabb;
        this.depth = depth;
        this.parentEntry = parentEntry;
        this.sourcePieceContext = sourcePieceContext;
        this.parentJunction = parentJunction;
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

    public void setPiece(PoolElementStructurePiece newPiece) {
        this.piece = newPiece;
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

    public PieceContext getSourcePieceContext() {
        return sourcePieceContext;
    }

    public AABB getPieceAabb() {
        return pieceAabb;
    }

    public JigsawJunction getParentJunction() {
        return parentJunction;
    }

    public Optional<ResourceLocation> getDeadendPool() {
        if (this.piece.getElement() instanceof YungJigsawSinglePoolElement yungSingleElement) {
            return yungSingleElement.getDeadendPool();
        }
        return Optional.empty();
    }

    public void setDelayGeneration(boolean delayGeneration) {
        this.delayGeneration = delayGeneration;
    }

    public boolean isDelayGeneration() {
        return this.delayGeneration;
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
                "boxOctree=" + boxOctree + ", " +
                "depth=" + depth + ']';
    }
}
