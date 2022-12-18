package com.yungnickyoung.minecraft.yungsapi.world.structure.targetselector;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;

import java.util.Optional;

/**
 * Target selector that returns the piece currently undergoing modification.
 */
public class SelfTargetSelector extends StructureTargetSelector {
    private static final SelfTargetSelector INSTANCE = new SelfTargetSelector();
    public static final Codec<SelfTargetSelector> CODEC = Codec.unit(() -> INSTANCE);

    public SelfTargetSelector() {
    }

    @Override
    public StructureTargetSelectorType<?> type() {
        return StructureTargetSelectorType.SELF;
    }

    @Override
    public Optional<PieceEntry> apply(StructureContext ctx) {
        if (ctx.pieceEntry() != null) {
            return Optional.of(ctx.pieceEntry());
        }
        return Optional.empty();
    }
}
