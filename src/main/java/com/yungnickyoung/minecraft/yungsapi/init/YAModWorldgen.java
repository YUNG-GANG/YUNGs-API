package com.yungnickyoung.minecraft.yungsapi.init;

import com.yungnickyoung.minecraft.yungsapi.mixin.ChunkGeneratorAccessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSpreadSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Optional;

public class YAModWorldgen {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(YAModWorldgen::deepCopyDimensionalSpacing);
    }

    /**
     * Deep copying the noise settings prevents bugs related to modded dimensions that use
     * the same noise settings as existing dimensions.
     * Credits to TelepathicGrunt.
     */
    private static void deepCopyDimensionalSpacing(final WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) event.getWorld();

            // Workaround for Terraforged
            ResourceLocation chunkGenResourceLocation = Registry.CHUNK_GENERATOR_CODEC.getKey(((ChunkGeneratorAccessor) serverWorld.getChunkProvider().generator).yungsapi_getCodec());
            if (chunkGenResourceLocation != null && chunkGenResourceLocation.getNamespace().equals("terraforged")) {
                return;
            }

            ChunkGenerator chunkGenerator = serverWorld.getChunkProvider().generator;

            // Grab old copy of stronghold spacing settings
            StructureSpreadSettings oldStrongholdSettings = chunkGenerator.func_235957_b_().func_236199_b_();

            // Make a deep copy and wrap it in an optional as DimensionStructuresSettings requires an optional
            Optional<StructureSpreadSettings> newStrongholdSettings = oldStrongholdSettings == null ?
                Optional.empty() :
                Optional.of(new StructureSpreadSettings(
                    oldStrongholdSettings.func_236660_a_(),
                    oldStrongholdSettings.func_236662_b_(),
                    oldStrongholdSettings.func_236663_c_()));

            // Create new deep copied DimensionStructuresSettings
            // We do not need to create a new structure spacing map instance here as our patch into
            // DimensionStructuresSettings will already create the new map instance for us.
            DimensionStructuresSettings newSettings = new DimensionStructuresSettings(newStrongholdSettings, chunkGenerator.func_235957_b_().func_236195_a_());

            ((ChunkGeneratorAccessor) chunkGenerator).yungsapi_setSettings(newSettings);
        }
    }
}
