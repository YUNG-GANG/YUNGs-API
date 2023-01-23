package com.yungnickyoung.minecraft.yungsapi.world.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

/**
 * Passes if the provided StructureContext has a specified rotation.
 */
public class RotationCondition extends StructureCondition {

    public static final Codec<RotationCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    Rotation.CODEC.listOf().fieldOf("rotations").forGetter(conditon -> conditon.validRotations))
            .apply(builder, RotationCondition::new));
    private final List<Rotation> validRotations;

    public RotationCondition(List<Rotation> validRotations) {
        this.validRotations = validRotations;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.ROTATION;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        Rotation rotation = ctx.rotation();
        return this.validRotations.contains(rotation);
    }
}
