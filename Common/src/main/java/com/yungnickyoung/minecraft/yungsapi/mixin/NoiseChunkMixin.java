package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverrideMask;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverrideMaskSupplier;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.SolidifyAquiferOverride;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(NoiseChunk.class)
public abstract class NoiseChunkMixin implements AquiferOverrideMaskSupplier {
    @Shadow
    @Final
    private DensityFunctions.BeardifierOrMarker beardifier;

    @Shadow
    public abstract int blockX();

    @Shadow
    public abstract int blockY();

    @Shadow
    public abstract int blockZ();

    @Shadow
    @Final
    private int cellCountY;

    @Shadow
    @Final
    private int cellNoiseMinY;

    @Shadow
    @Final
    private int cellHeight;

    @Unique
    private ThreadLocal<AquiferOverrideMask> aquiferOverrideMask = new ThreadLocal<>();

    @Unique
    private BlockState defaultBlockState;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void yungsapi_attachNoiseChunkToBeardifier(int cellCountXZ, RandomState randomState, int chunkMinBlockX, int chunkMinBlockZ, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifier, NoiseGeneratorSettings settings, Aquifer.FluidPicker globalFluidPicker, Blender blender, CallbackInfo ci) {
        if (this.beardifier instanceof EnhancedBeardifierData enhancedBeardifierData) {
            enhancedBeardifierData.setNoiseChunk((NoiseChunk) (Object) this);
        }
        this.defaultBlockState = settings.defaultBlock();
    }

    /**
     * Modify water/lava from aquifers in positions that are also carved by EnhancedTerrainAdaptations.
     * Note that this only ends up being applied if the EnhancedTerrainAdaptation has the "aquifer_override" property set.
     */
    @Inject(method = "getInterpolatedState", at = @At("RETURN"), cancellable = true)
    private void yungsapi_dontFillMarkedPositions(CallbackInfoReturnable<BlockState> cir) {
        BlockState retVal = cir.getReturnValue();
        if (retVal != null && (retVal.is(Blocks.WATER) || retVal.is(Blocks.LAVA))) {

            int height = this.cellCountY * this.cellHeight;
            int minY = this.cellNoiseMinY * this.cellHeight;

            AquiferOverrideMask mask = this.getOrCreateAquiferOverrideMask(() -> new AquiferOverrideMask(height, minY));

            // Special handling for SolidifyAquiferOverride
            if (mask.getAquiferOverride() instanceof SolidifyAquiferOverride solidifyAquiferOverride) {
                solidifyAquiferOverride.setSolidBlockState(this.defaultBlockState);
            }

            BlockState blockState = mask.getBlockStateForPos(this.blockX(), this.blockY(), this.blockZ(), retVal);
            cir.setReturnValue(blockState);
        }
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
