package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;

/**
 * Passes if the mod with the provided mod ID is loaded.
 */
public class ModLoadedCondition extends StructureCondition {
    public static final Codec<ModLoadedCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    Codec.STRING.fieldOf("modid").forGetter(conditon -> conditon.modId))
            .apply(builder, ModLoadedCondition::new));

    private final String modId;

    public ModLoadedCondition(String modId) {
        this.modId = modId;
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.MOD_LOADED;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        return Services.PLATFORM.isModLoaded(this.modId);
    }
}
