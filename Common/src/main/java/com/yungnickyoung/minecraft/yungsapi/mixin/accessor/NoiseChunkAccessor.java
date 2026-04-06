package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.world.level.levelgen.NoiseChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NoiseChunk.class)
public interface NoiseChunkAccessor {
    @Accessor("cellHeight")
    int getCellHeight();

    @Accessor("cellCountY")
    int getCellCountY();

    @Accessor("cellNoiseMinY")
    int getCellNoiseMinY();
}
