package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBiome;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Registration of Biomes.
 */
public class BiomeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.BIOMES.stream()
                .filter(data -> !data.processed())
                .forEach(BiomeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterBiome autoRegisterBiome = (AutoRegisterBiome) data.object();
        Biome biome = autoRegisterBiome.get();
        ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, data.name());
        autoRegisterBiome.setResourceKey(key);

        // Register biome
        BuiltinRegistries.register(BuiltinRegistries.BIOME, key, biome);
        data.markProcessed();
    }
}
