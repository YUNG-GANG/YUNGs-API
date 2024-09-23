package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterParticleType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registration of ParticleTypes.
 */
public class ParticleTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.PARTICLE_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(ParticleTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterParticleType<?> autoRegisterParticleType = (AutoRegisterParticleType<?>) data.object();
        ParticleType<?> particleType = autoRegisterParticleType.get();

        // Register
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, data.name(), particleType);
        data.markProcessed();
    }
}
