package com.yungnickyoung.minecraft.yungsapi.world.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.codec.CodecHelper;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

/**
 * Passes if the provided StructureContext has a specified rotation.
 */
public class RotationCondition extends StructureCondition {

    public static final Codec<RotationCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    CodecHelper.ROTATION_CODEC.listOf().fieldOf("rotations").forGetter(conditon -> conditon.validRotations))
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
        // Extract args from context
        Rotation rotation = ctx.rotation();

        // Abort if missing any args
        if (rotation == null) YungsApiCommon.LOGGER.error("Missing required field 'rotation' for rotation condition!");
        if (rotation == null) return false;

        return this.validRotations.contains(rotation);
    }
}
