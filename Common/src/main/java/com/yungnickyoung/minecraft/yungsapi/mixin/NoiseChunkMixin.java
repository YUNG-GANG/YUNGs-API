package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverrideMask;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverrideMaskSupplier;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.SolidifyAquiferOverride;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(NoiseChunk.class)
public abstract class NoiseChunkMixin implements AquiferOverrideMaskSupplier {
    @Unique
    private ThreadLocal<AquiferOverrideMask> aquiferOverrideMask = new ThreadLocal<>();

    @Unique
    private BlockState defaultBlockState;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void yungsapi_attachNoiseChunkToBeardifier(RandomState randomState, Beardifier beardifier, NoiseGeneratorSettings settings, Aquifer.FluidPicker globalFluidPicker, Blender blender, DensityVolume volume, CallbackInfo ci) {
        if (beardifier instanceof EnhancedBeardifierData enhancedBeardifierData) {
            enhancedBeardifierData.yungsapi_setNoiseChunk((NoiseChunk) (Object) this);
        }
        this.defaultBlockState = settings.defaultBlock();
    }

    @Unique
    @Override
    public BlockState yungsapi_applyAquiferOverride(int x, int y, int z, BlockState state) {
        AquiferOverrideMask mask = this.aquiferOverrideMask.get();
        if (mask != null && (state.is(Blocks.WATER) || state.is(Blocks.LAVA))) {
            if (mask.getAquiferOverride() instanceof SolidifyAquiferOverride solidifyAquiferOverride) {
                solidifyAquiferOverride.setSolidBlockState(this.defaultBlockState);
            }
            return mask.getBlockStateForPos(x, y, z, state);
        }
        return state;
    }

    @Unique
    @Override
    public AquiferOverrideMask getOrCreateAquiferOverrideMask(Supplier<AquiferOverrideMask> aquiferOverrideMaskSupplier) {
        if (this.aquiferOverrideMask.get() == null) {
            this.aquiferOverrideMask.set(aquiferOverrideMaskSupplier.get());
        }
        return this.aquiferOverrideMask.get();
    }
}
