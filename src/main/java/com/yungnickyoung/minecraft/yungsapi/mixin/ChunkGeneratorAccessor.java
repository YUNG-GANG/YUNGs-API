package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Mutable
    @Accessor("settings")
    void yungsapi_setSettings(DimensionStructuresSettings dimensionStructuresSettings);

    @Invoker("func_230347_a_")
    Codec<ChunkGenerator> yungsapi_getCodec();
}
