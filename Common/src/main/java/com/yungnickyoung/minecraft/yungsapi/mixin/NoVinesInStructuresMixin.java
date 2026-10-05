package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.module.TagModule;
import com.yungnickyoung.minecraft.yungsapi.util.MixinUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.VinesFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VinesFeature.class)
public class NoVinesInStructuresMixin {
    @Inject(method = "place", at = @At(value = "HEAD"), cancellable = true)
    private void yungsapi_noVinesInStructures(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
        if (!(level instanceof WorldGenRegion worldGenRegion)) return;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            mutable.set(origin).move(direction);
            if (MixinUtils.isPositionInTaggedStructure(worldGenRegion, mutable, TagModule.NO_VINES)) {
                cir.setReturnValue(false);
            }
        }
    }
}
