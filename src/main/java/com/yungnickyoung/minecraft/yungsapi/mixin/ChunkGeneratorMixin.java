package com.yungnickyoung.minecraft.yungsapi.mixin;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * @author TelepathicGrunt
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Mutable
    @Final
    @Shadow
    private StructuresConfig structuresConfig;

    @Inject(method = "<init>(Lnet/minecraft/world/biome/source/BiomeSource;Lnet/minecraft/world/biome/source/BiomeSource;Lnet/minecraft/world/gen/chunk/StructuresConfig;J)V",
        at = @At(value = "RETURN"))
    private void yungsapi_deepCopyNoiseSettings(BiomeSource populationSource, BiomeSource biomeSource, StructuresConfig structuresConfig, long worldSeed, CallbackInfo ci) {
        // Grab old copy of stronghold spacing settings
        StrongholdConfig oldStrongholdSettings = structuresConfig.getStronghold();

        // Make a deep copy and wrap it in an optional as DimensionStructuresSettings requires an optional
        Optional<StrongholdConfig> newStrongholdSettings = oldStrongholdSettings == null ?
            Optional.empty() :
            Optional.of(new StrongholdConfig(
                oldStrongholdSettings.getDistance(),
                oldStrongholdSettings.getSpread(),
                oldStrongholdSettings.getCount()));

        // Create new deep copied DimensionStructuresSettings
        // We do not need to create a new structure spacing map instance here as our patch into
        // DimensionStructuresSettings will already create the new map instance for us.
        this.structuresConfig = new StructuresConfig(newStrongholdSettings, structuresConfig.getStructures());
    }
}
