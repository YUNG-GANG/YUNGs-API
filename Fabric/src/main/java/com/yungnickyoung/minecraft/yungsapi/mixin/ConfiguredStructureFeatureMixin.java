package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.api.YungJigsawConfig;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.EnhancedTerrainAdaptation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConfiguredStructureFeature.class)
public class ConfiguredStructureFeatureMixin<FC extends FeatureConfiguration, F extends StructureFeature<FC>> {
    @Shadow @Final public FC config;

    @Inject(method = "adjustBoundingBox", at = @At("RETURN"), cancellable = true)
    private void yungsapi_adjustBoundingBox(BoundingBox boundingBox, CallbackInfoReturnable<BoundingBox> cir) {
        if (this.config instanceof YungJigsawConfig yungConfig && yungConfig.getEnhancedTerrainAdaptation() != EnhancedTerrainAdaptation.NONE) {
            cir.setReturnValue(boundingBox.inflatedBy(yungConfig.getEnhancedTerrainAdaptation().getKernelRadius()));
        }
    }
}
