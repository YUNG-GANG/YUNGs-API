package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterParticleType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of ParticleTypes.
 */
public class ParticleTypeModuleForge {
    private static final Map<String, DeferredRegister<ParticleType<?>>> registersByModId = new HashMap<>();

    public static void processEntries() {
        AutoRegistrationManager.PARTICLE_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(ParticleTypeModuleForge::register);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void register(AutoRegisterField data) {
        // Create & register deferred registry for current mod, if necessary
        String modId = data.name().getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<ParticleType<?>> deferredRegister = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, modId);
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
            registersByModId.put(modId, deferredRegister);
        }

        AutoRegisterParticleType autoRegisterParticleType = (AutoRegisterParticleType) data.object();
        Supplier<ParticleType<?>> particleTypeSupplier = autoRegisterParticleType.getSupplier();

        // Register
        DeferredRegister<ParticleType<?>> deferredRegister = registersByModId.get(modId);
        RegistryObject<ParticleType<?>> registryObject = deferredRegister.register(data.name().getPath(), particleTypeSupplier);

        // Update the supplier to use the RegistryObject so that it will be properly updated later on
        autoRegisterParticleType.setSupplier(registryObject);

        data.markProcessed();
    }
}
