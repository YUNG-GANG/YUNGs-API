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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects behavior required for using {@link EnhancedTerrainAdaptation} with {@link YungJigsawStructure}.
 * <p>
 * Uses a higher priority so that it applies after <a href="https://github.com/FinnSetchell/MoogsStructureLib/blob/1.21.11/common/src/main/java/com/finndog/moogs_structures/mixins/terrainadaptation/BeardifierMixin.java#L32">
 * moog's structure lib's similar mixin</a>. Since moog wraps the beardifier in a new one and we modify it in-place, applying our
 * change after moog's allows both mod's changes to apply.
 */
@Mixin(value = Beardifier.class, priority = 1100)
public abstract class BeardifierMixin implements EnhancedBeardifierData, DensityFunctions.BeardifierOrMarker {
    @Unique
    private @Nullable ObjectList<EnhancedJigsawJunction> enhancedJunctions;

    @Unique
    private @Nullable ObjectList<EnhancedBeardifierRigid> enhancedPieces;

    @Unique
    private @Nullable NoiseChunk noiseChunk;

    @Inject(method = "forStructuresInChunk", at = @At("RETURN"), cancellable = true)
    private static void yungsapi_supportCustomTerrainAdaptations(final StructureManager structureManager, final ChunkPos chunkPos, final CallbackInfoReturnable<Beardifier> cir) {
        cir.setReturnValue(EnhancedBeardifierHelper.forStructuresInChunk(structureManager, chunkPos, cir.getReturnValue()));
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
