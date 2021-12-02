package com.yungnickyoung.minecraft.yungsapi.mixin;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
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
    private StructureSettings settings;

    @Inject(
        method = "<init>(Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/levelgen/StructureSettings;J)V",
        at = @At(value = "RETURN"))
    private void yungsapi_deepCopyNoiseSettings(BiomeSource populationSource, BiomeSource biomeSource, StructureSettings structureSettings, long worldSeed, CallbackInfo ci) {
        // Grab old copy of stronghold spacing settings
        StrongholdConfiguration oldStrongholdSettings = structureSettings.stronghold();

        // Make a deep copy and wrap it in an optional as DimensionStructuresSettings requires an optional
        Optional<StrongholdConfiguration> newStrongholdSettings = oldStrongholdSettings == null ?
            Optional.empty() :
            Optional.of(new StrongholdConfiguration(
                oldStrongholdSettings.distance(),
                oldStrongholdSettings.spread(),
                oldStrongholdSettings.count()));

        // Create new deep copied DimensionStructuresSettings
        // We do not need to create a new structure spacing map instance here as our patch into
        // DimensionStructuresSettings will already create the new map instance for us.
        this.settings = new StructureSettings(newStrongholdSettings, structureSettings.structureConfig());
    }
}
