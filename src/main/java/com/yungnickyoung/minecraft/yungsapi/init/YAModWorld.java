package com.yungnickyoung.minecraft.yungsapi.init;

import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.ChunkGeneratorAccessor;
import com.yungnickyoung.minecraft.yungsapi.util.NoiseSettingsDeepCopier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class YAModWorld {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, YAModWorld::deepCopyDimensionalSpacing);
    }

    /**
     * This is a high priority WorldEvent.Load event instead of a mixin because the mixin form is too early
     * and could break a potential future Forge PR that is currently being worked on.
     */
    private static void deepCopyDimensionalSpacing(final WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel serverLevel) {
            // Workaround for Terraforged
            ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey(((ChunkGeneratorAccessor) serverLevel.getChunkSource().getGenerator()).invokeCodec());
            if (cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
            ((ChunkGeneratorAccessor) chunkGenerator).setSettings(NoiseSettingsDeepCopier.deepCopyDimensionStructuresSettings(chunkGenerator.getSettings()));
        }
    }
}
