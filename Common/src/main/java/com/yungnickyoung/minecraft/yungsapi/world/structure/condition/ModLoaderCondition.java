package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Passes if the game is loaded w/ the specified mod loader.
 * String values are case-insensitive.
 */
public class ModLoaderCondition extends StructureCondition {

    public static final Codec<ModLoaderCondition> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    Codec.STRING.listOf().fieldOf("loaders").forGetter(conditon -> conditon.validLoaders))
            .apply(builder, ModLoaderCondition::new));
    private final List<String> validLoaders;

    public ModLoaderCondition(List<String> validLoaders) {
        this.validLoaders = validLoaders.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @Override
    public StructureConditionType<?> type() {
        return StructureConditionType.MOD_LOADER;
    }

    @Override
    public boolean passes(StructureContext ctx) {
        String loader = Services.PLATFORM.getPlatformName();
        return this.validLoaders.contains(loader.toLowerCase());
    }
}
