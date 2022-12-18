package com.yungnickyoung.minecraft.yungsapi.world.structure.targetselector;

import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;

import java.util.Optional;

/**
 * A serializable class used for selecting a {@link PieceEntry} during world generation.
 */
public abstract class StructureTargetSelector {
    abstract public StructureTargetSelectorType<?> type();
    abstract public Optional<PieceEntry> apply(StructureContext ctx);
}
