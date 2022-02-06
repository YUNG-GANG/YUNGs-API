package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.google.common.collect.ImmutableMap;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.ChunkGeneratorAccessor;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StructureSettingsAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * Deep copies dimension structure settings.
 * This is necessary to allow for dimension whitelisting/blacklisting, since each dimension
 * is not normally guaranteed to have its own settings object.
 * @author TelepathicGrunt
 */
@Mixin(ServerLevel.class)
public abstract class DeepCopyStructureSettingsMixin {
    @Shadow public abstract ServerChunkCache getChunkSource();

    @Inject(
        method = "<init>",
        at = @At(value = "RETURN"))
    private void yungsapi_deepCopyNoiseSettings(MinecraftServer minecraftServer, Executor executor,
                                                LevelStorageSource.LevelStorageAccess levelStorageAccess,
                                                ServerLevelData serverLevelData, ResourceKey resourceKey,
                                                DimensionType dimensionType, ChunkProgressListener chunkProgressListener,
                                                ChunkGenerator chunkGenerator, boolean bl, long l, List list, boolean bl2,
                                                CallbackInfo ci) {
        StructureSettings settings = getChunkSource().getGenerator().getSettings();

        // Grab old copy of stronghold spacing settings
        StrongholdConfiguration oldStrongholdSettings = settings.stronghold();

        // Make a deep copy and wrap it in an optional as DimensionStructuresSettings requires an optional
        Optional<StrongholdConfiguration> newStrongholdSettings = oldStrongholdSettings == null ?
            Optional.empty() :
            Optional.of(new StrongholdConfiguration(
                oldStrongholdSettings.distance(),
                oldStrongholdSettings.spread(),
                oldStrongholdSettings.count()));

        // Create new deep copied DimensionStructuresSettings
        // We do not need to create a new structure spacing map instance here as our patch into
        // StructureSettings will already create the new map instance for us.
        StructureSettings newStructureSettings = new StructureSettings(newStrongholdSettings, settings.structureConfig());
        ((StructureSettingsAccessor)newStructureSettings).setConfiguredStructures(ImmutableMap.copyOf(((StructureSettingsAccessor)settings).getConfiguredStructures()));
        ((ChunkGeneratorAccessor)getChunkSource().getGenerator()).setSettings(newStructureSettings);
    }
}
