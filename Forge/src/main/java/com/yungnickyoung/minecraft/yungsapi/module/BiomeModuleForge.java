package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBiome;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of Biomes.
 */
public class BiomeModuleForge {
    private static final Map<String, DeferredRegister<Biome>> registersByModId = new HashMap<>();

    public static void processEntries() {
        AutoRegistrationManager.BIOMES.stream()
                .filter(data -> !data.processed())
                .forEach(BiomeModuleForge::register);
    }

    private static void register(AutoRegisterField data) {
        // Create & register deferred registry for current mod, if necessary
        String modId = data.name().getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<Biome> deferredRegister = DeferredRegister.create(Registry.BIOME_REGISTRY, modId);
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
            registersByModId.put(modId, deferredRegister);
        }

        AutoRegisterBiome autoRegisterBiome = (AutoRegisterBiome) data.object();
        Supplier<Biome> biomeSupplier = autoRegisterBiome.getSupplier();

        // Register
        DeferredRegister<Biome> deferredRegister = registersByModId.get(modId);
        RegistryObject<Biome> registryObject = deferredRegister.register(data.name().getPath(), biomeSupplier);

        // Update the supplier to use the RegistryObject so that it will be properly updated later on
        autoRegisterBiome.setSupplier(registryObject);

        ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, data.name());
        autoRegisterBiome.setResourceKey(key);

        data.markProcessed();
    }
}
