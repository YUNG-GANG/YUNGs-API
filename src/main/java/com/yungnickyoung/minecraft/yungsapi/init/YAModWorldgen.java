//package com.yungnickyoung.minecraft.yungsapi.init;
//
//import com.yungnickyoung.minecraft.yungsapi.mixin.ChunkGeneratorAccessor;
//import net.minecraft.core.Registry;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.StructureSettings;
//import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.event.world.WorldEvent;
//
//import java.util.Optional;
//
//public class YAModWorldgen {
//    public static void init() {
//        MinecraftForge.EVENT_BUS.addListener(YAModWorldgen::deepCopyDimensionalSpacing);
//    }
//
//    /**
//     * Deep copying the noise settings prevents bugs related to modded dimensions that use
//     * the same noise settings as existing dimensions.
//     * Credits to TelepathicGrunt.
//     */
//    private static void deepCopyDimensionalSpacing(final WorldEvent.Load event) {
//        if (event.getWorld() instanceof ServerLevel serverWorld) {
//
//            // Workaround for Terraforged
//            ResourceLocation chunkGenResourceLocation = Registry.CHUNK_GENERATOR.getKey(((ChunkGeneratorAccessor) serverWorld.getChunkSource().generator).yungsapi_getCodec());
//            if (chunkGenResourceLocation != null && chunkGenResourceLocation.getNamespace().equals("terraforged")) {
//                return;
//            }
//
//            ChunkGenerator chunkGenerator = serverWorld.getChunkSource().generator;
//
//            // Grab old copy of stronghold spacing settings
//            StrongholdConfiguration oldStrongholdSettings = chunkGenerator.getSettings().stronghold();
//
//            // Make a deep copy and wrap it in an optional as DimensionStructuresSettings requires an optional
//            Optional<StrongholdConfiguration> newStrongholdSettings = oldStrongholdSettings == null ?
//                Optional.empty() :
//                Optional.of(new StrongholdConfiguration(
//                    oldStrongholdSettings.distance(),
//                    oldStrongholdSettings.spread(),
//                    oldStrongholdSettings.count()));
//
//            // Create new deep copied DimensionStructuresSettings
//            // We do not need to create a new structure spacing map instance here as our patch into
//            // DimensionStructuresSettings will already create the new map instance for us.
//            StructureSettings newSettings = new StructureSettings(newStrongholdSettings, chunkGenerator.getSettings().structureConfig());
//
//            ((ChunkGeneratorAccessor) chunkGenerator).yungsapi_setSettings(newSettings);
//        }
//    }
//}
