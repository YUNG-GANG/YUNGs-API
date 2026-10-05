package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverrideMaskSupplier;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
    @Redirect(method = "doFill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Aquifer;computeSubstance(IIID)Lnet/minecraft/world/level/block/state/BlockState;"))
    private @Nullable BlockState yungsapi_applyAquiferOverride(Aquifer aquifer, int x, int y, int z, double density, NoiseChunk noiseChunk, ChunkAccess chunk) {
        BlockState state = aquifer.computeSubstance(x, y, z, density);
        return state == null ? null : ((AquiferOverrideMaskSupplier) noiseChunk).yungsapi_applyAquiferOverride(x, y, z, state);
    }
}
