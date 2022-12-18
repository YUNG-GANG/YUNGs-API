package com.yungnickyoung.minecraft.yungsapi.world.structure.action;

import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;

/**
 * A serializable class used for modifying pieces of Jigsaw structures during world generation.
 */
public abstract class StructureAction {
    abstract public StructureActionType<?> type();
    abstract public void apply(StructureContext ctx, PieceEntry targetPieceEntry);
}
