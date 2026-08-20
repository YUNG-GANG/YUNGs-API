package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierData;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierHelper;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierRigid;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedJigsawJunction;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects behavior required for using {@link EnhancedTerrainAdaptation} with {@link YungJigsawStructure}.
 */
@Mixin(Beardifier.class)
@NullMarked
public abstract class BeardifierMixin implements EnhancedBeardifierData, DensityFunctions.BeardifierOrMarker {
    @Unique
    private @Nullable ObjectList<EnhancedJigsawJunction> enhancedJunctions;

    @Unique
    private @Nullable ObjectList<EnhancedBeardifierRigid> enhancedPieces;

    @Unique
    private @Nullable NoiseChunk noiseChunk;

    @WrapMethod(method = "forStructuresInChunk")
    private static Beardifier yungsapi_supportCustomTerrainAdaptations(final StructureManager structureManager, final ChunkPos chunkPos, final Operation<Beardifier> original) {
        return EnhancedBeardifierHelper.forStructuresInChunk(structureManager, chunkPos, original.call(structureManager, chunkPos));
    }

    @Inject(method = "fillArray", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;fill([DD)V"), cancellable = true)
    private void yungsapi_fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider, final CallbackInfo ci) {
        if (this.enhancedPieces != null && this.enhancedJunctions != null
            && (!this.enhancedPieces.isEmpty() || !this.enhancedJunctions.isEmpty())) {
            DensityFunctions.BeardifierOrMarker.super.fillArray(output, contextProvider);
            ci.cancel();
        }
    }

    @Inject(method = "compute", at = @At("RETURN"), cancellable = true)
    public void yungsapi_calculateDensity(DensityFunction.FunctionContext ctx, CallbackInfoReturnable<Double> cir) {
        double density = cir.getReturnValue();
        double newDensity = EnhancedBeardifierHelper.computeDensity(ctx, density, this);
        cir.setReturnValue(newDensity);
    }

    @Override
    public @Nullable ObjectListIterator<EnhancedBeardifierRigid> yungsapi_getEnhancedPieceIterator() {
        return this.enhancedPieces == null ? null : this.enhancedPieces.iterator();
    }

    @Override
    public void yungsapi_setEnhancedPieces(ObjectList<EnhancedBeardifierRigid> enhancedPieces) {
        this.enhancedPieces = enhancedPieces;
    }

    @Override
    public @Nullable ObjectListIterator<EnhancedJigsawJunction> yungsapi_getEnhancedJunctionIterator() {
        return this.enhancedJunctions == null ? null : this.enhancedJunctions.iterator();
    }

    @Override
    public void yungsapi_setEnhancedJunctions(ObjectList<EnhancedJigsawJunction> enhancedJunctions) {
        this.enhancedJunctions = enhancedJunctions;
    }

    @Override
    public @Nullable NoiseChunk yungsapi_getNoiseChunk() {
        return this.noiseChunk;
    }

    @Override
    public void yungsapi_setNoiseChunk(NoiseChunk noiseChunk) {
        this.noiseChunk = noiseChunk;
    }
}
