package com.yungnickyoung.minecraft.yungsapi.world.structure.action;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.PieceEntry;

/**
 * Delays generation of target piece(s) to after the rest of the structure's pieces.
 */
public class DelayGenerationAction extends StructureAction {
    private static final DelayGenerationAction INSTANCE = new DelayGenerationAction();
    public static final MapCodec<DelayGenerationAction> CODEC = MapCodec.unit(() -> INSTANCE);

    public DelayGenerationAction() {
    }

    @Override
    public StructureActionType<?> type() {
        return StructureActionType.DELAY_GENERATION;
    }

    @Override
    public void apply(StructureContext ctx, PieceEntry targetPieceEntry) {
        targetPieceEntry.setDelayGeneration(true);
    }
}
