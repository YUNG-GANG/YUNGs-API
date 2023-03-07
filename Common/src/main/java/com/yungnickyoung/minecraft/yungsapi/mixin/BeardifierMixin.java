package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierData;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierHelper;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierRigid;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedJigsawJunction;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects behavior required for using {@link EnhancedTerrainAdaptation} with {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawConfig}.
 */
@Mixin(Beardifier.class)
public class BeardifierMixin implements EnhancedBeardifierData {
    @Unique
    private ObjectListIterator<EnhancedJigsawJunction> enhancedJunctionIterator;
    @Unique
    private ObjectListIterator<EnhancedBeardifierRigid> enhancedRigidIterator;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void yungsapi_supportCustomTerrainAdaptations(StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess, CallbackInfo ci) {
        EnhancedBeardifierHelper.forStructuresInChunk(structureFeatureManager, chunkAccess, this);
    }

    @Inject(method = "compute", at = @At("RETURN"), cancellable = true)
    public void yungsapi_calculateDensity(DensityFunction.FunctionContext ctx, CallbackInfoReturnable<Double> cir) {
        double density = cir.getReturnValue();
        double newDensity = EnhancedBeardifierHelper.computeDensity(ctx, density, this);
        cir.setReturnValue(newDensity);
    }

    @Override
    public ObjectListIterator<EnhancedBeardifierRigid> getEnhancedRigidIterator() {
        return this.enhancedRigidIterator;
    }

    @Override
    public void setEnhancedRigidIterator(ObjectListIterator<EnhancedBeardifierRigid> enhancedRigidIterator) {
        this.enhancedRigidIterator = enhancedRigidIterator;
    }

    @Override
    public ObjectListIterator<EnhancedJigsawJunction> getEnhancedJunctionIterator() {
        return enhancedJunctionIterator;
    }

    @Override
    public void setEnhancedJunctionIterator(ObjectListIterator<EnhancedJigsawJunction> enhancedJunctionIterator) {
        this.enhancedJunctionIterator = enhancedJunctionIterator;
    }
}
