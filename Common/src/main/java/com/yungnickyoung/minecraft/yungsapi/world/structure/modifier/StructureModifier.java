package com.yungnickyoung.minecraft.yungsapi.world.structure.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.world.structure.action.StructureAction;
import com.yungnickyoung.minecraft.yungsapi.world.structure.action.StructureActionType;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.condition.StructureCondition;
import com.yungnickyoung.minecraft.yungsapi.world.condition.StructureConditionType;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;
import com.yungnickyoung.minecraft.yungsapi.world.structure.targetselector.StructureTargetSelector;
import com.yungnickyoung.minecraft.yungsapi.world.structure.targetselector.StructureTargetSelectorType;

import java.util.Optional;

/**
 * Applies a {@link StructureAction} to a target determined by an associated {@link StructureTargetSelector},
 * only if an associated {@link StructureCondition} passes.
 * This is really just a convenience wrapper for associating actions, target selectors, and conditions;
 * thereby easing JSON (de)serialization.
 */
public class StructureModifier {
    public static final Codec<StructureModifier> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    StructureConditionType.CONDITION_CODEC.fieldOf("condition").forGetter(modifier -> modifier.condition),
                    StructureActionType.ACTION_CODEC.fieldOf("action").forGetter(modifier -> modifier.action),
                    StructureTargetSelectorType.TARGET_SELECTOR_CODEC.fieldOf("target_selector").forGetter(modifier -> modifier.targetSelector))
            .apply(builder, StructureModifier::new));

    private final StructureCondition condition;
    private final StructureAction action;
    private final StructureTargetSelector targetSelector;

    public StructureModifier(StructureCondition condition, StructureAction action, StructureTargetSelector targetSelector) {
        this.condition = condition;
        this.action = action;
        this.targetSelector = targetSelector;
    }

    public boolean apply(StructureContext structureContext) {
        // Validate condition
        if (!this.condition.passes(structureContext)) {
            return false;
        }

        // Validate target
        Optional<PieceEntry> target = this.targetSelector.apply(structureContext);
        if (target.isEmpty()) {
            return false;
        }

        // Apply action
        this.action.apply(structureContext, target.get());
        return true;
    }
}
