package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Supplier;

public class AutoRegisterBiome extends AutoRegisterEntry<Biome> {
    private ResourceKey<Biome> resourceKey;

    public static AutoRegisterBiome of(Supplier<Biome> biomeSupplier) {
        return new AutoRegisterBiome(biomeSupplier);
    }

    private AutoRegisterBiome(Supplier<Biome> biomeSupplier) {
        super(biomeSupplier);
    }

    public ResourceKey<Biome> getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(ResourceKey<Biome> resourceKey) {
        this.resourceKey = resourceKey;
    }
}
