package com.yungnickyoung.minecraft.yungsapi.world.structure.targetselector;

import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;

import java.util.List;

/**
 * A serializable class used for selecting a list of {@link PieceEntry}s during world generation.
 */
public abstract class StructureTargetSelector {
    abstract public StructureTargetSelectorType<?> type();
    abstract public List<PieceEntry> apply(StructureContext ctx);
}
