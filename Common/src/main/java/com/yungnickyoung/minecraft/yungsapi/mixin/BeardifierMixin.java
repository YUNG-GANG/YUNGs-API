package com.yungnickyoung.minecraft.yungsapi.mixin;

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
    private ObjectList<EnhancedJigsawJunction> enhancedJunctions;

    @Unique
    private ObjectList<EnhancedBeardifierRigid> enhancedPieces;

    @Unique
    private NoiseChunk noiseChunk;

    @Inject(method = "forStructuresInChunk", at = @At("RETURN"))
    private static Beardifier yungsapi_supportCustomTerrainAdaptations(final StructureManager structureManager, final ChunkPos chunkPos, final CallbackInfoReturnable<Beardifier> cir) {
        return EnhancedBeardifierHelper.forStructuresInChunk(structureManager, chunkPos, cir.getReturnValue());
    }

    @Inject(method = "compute", at = @At("RETURN"), cancellable = true)
    public void yungsapi_calculateDensity(DensityFunction.FunctionContext ctx, CallbackInfoReturnable<Double> cir) {
        double density = cir.getReturnValue();
        double newDensity = EnhancedBeardifierHelper.computeDensity(ctx, density, this);
        cir.setReturnValue(newDensity);
    }

    @Override
    public ObjectListIterator<EnhancedBeardifierRigid> yungsapi_getEnhancedPieceIterator() {
        return this.enhancedPieces.iterator();
    }

    @Override
    public void yungsapi_setEnhancedPieces(ObjectList<EnhancedBeardifierRigid> enhancedPieces) {
        this.enhancedPieces = enhancedPieces;
    }

    @Override
    public ObjectListIterator<EnhancedJigsawJunction> yungsapi_getEnhancedJunctionIterator() {
        return enhancedJunctions.iterator();
    }

    @Override
    public void yungsapi_setEnhancedJunctions(ObjectList<EnhancedJigsawJunction> enhancedJunctions) {
        this.enhancedJunctions = enhancedJunctions;
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
