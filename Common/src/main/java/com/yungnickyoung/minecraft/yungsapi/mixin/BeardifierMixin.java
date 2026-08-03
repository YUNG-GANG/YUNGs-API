package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierData;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierHelper;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierRigid;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedJigsawJunction;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
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
public abstract class BeardifierMixin implements EnhancedBeardifierData, DensityFunctions.BeardifierOrMarker {
    @Unique
    private ObjectListIterator<EnhancedJigsawJunction> enhancedJunctionIterator;

    @Unique
    private ObjectListIterator<EnhancedBeardifierRigid> enhancedPieceIterator;

    @Unique
    private NoiseChunk noiseChunk;

    @WrapMethod(method = "forStructuresInChunk")
    private static Beardifier yungsapi_supportCustomTerrainAdaptations(final StructureManager structureManager, final ChunkPos chunkPos, final Operation<Beardifier> original) {
        return EnhancedBeardifierHelper.forStructuresInChunk(structureManager, chunkPos, original.call(structureManager, chunkPos));
    }

    @Inject(method = "fillArray", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;fill([DD)V"), cancellable = true)
    private void yungsapi_fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider, final CallbackInfo ci) {
        if (this.enhancedPieceIterator != null && this.enhancedJunctionIterator != null
            && (this.enhancedPieceIterator.hasNext() || this.enhancedJunctionIterator.hasNext())) {
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
    public ObjectListIterator<EnhancedBeardifierRigid> yungsapi_getEnhancedPieceIterator() {
        return this.enhancedPieceIterator;
    }

    @Override
    public void yungsapi_setEnhancedPieceIterator(ObjectListIterator<EnhancedBeardifierRigid> enhancedPieceIterator) {
        this.enhancedPieceIterator = enhancedPieceIterator;
    }

    @Override
    public ObjectListIterator<EnhancedJigsawJunction> yungsapi_getEnhancedJunctionIterator() {
        return enhancedJunctionIterator;
    }

    @Override
    public void yungsapi_setEnhancedJunctionIterator(ObjectListIterator<EnhancedJigsawJunction> enhancedJunctionIterator) {
        this.enhancedJunctionIterator = enhancedJunctionIterator;
    }

    @Override
    public NoiseChunk yungsapi_getNoiseChunk() {
        return this.noiseChunk;
    }

    @Override
    public void yungsapi_setNoiseChunk(NoiseChunk noiseChunk) {
        this.noiseChunk = noiseChunk;
    }
}
