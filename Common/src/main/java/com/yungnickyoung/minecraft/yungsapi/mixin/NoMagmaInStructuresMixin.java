package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.module.TagModule;
import com.yungnickyoung.minecraft.yungsapi.util.MixinUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.UnderwaterMagmaFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnderwaterMagmaFeature.class)
public abstract class NoMagmaInStructuresMixin {
    @Inject(
            method = "place",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void yungsapi_noMagmaInStructures(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
        if (!(level instanceof WorldGenRegion worldGenRegion)) return;

        if (MixinUtils.isPositionInTaggedStructure(worldGenRegion, origin, TagModule.NO_MAGMA)) {
            cir.setReturnValue(false);
        }
    }
}
