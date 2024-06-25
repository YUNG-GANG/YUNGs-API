package com.yungnickyoung.minecraft.yungsapi.world.structure.targetselector;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.PieceEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Target selector that returns the piece currently undergoing modification.
 */
public class SelfTargetSelector extends StructureTargetSelector {
    private static final SelfTargetSelector INSTANCE = new SelfTargetSelector();
    public static final MapCodec<SelfTargetSelector> CODEC = MapCodec.unit(() -> INSTANCE);

    public SelfTargetSelector() {
    }

    @Override
    public StructureTargetSelectorType<?> type() {
        return StructureTargetSelectorType.SELF;
    }

    @Override
    public List<PieceEntry> apply(StructureContext ctx) {
        List<PieceEntry> list = new ArrayList<>();

        if (ctx.pieceEntry() != null) {
            list.add(ctx.pieceEntry());
        }

        return list;
    }
}
