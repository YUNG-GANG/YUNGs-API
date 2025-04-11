package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterParticleType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;

/**
 * Registration of particle types.
 */
public class ParticleTypeModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildAutoRegistrar(Registries.PARTICLE_TYPE, AutoRegistrationManager.PARTICLE_TYPES, ParticleTypeModuleNeoForge::buildParticleType));
    }

    private static ParticleType<?> buildParticleType(AutoRegisterField data) {
        // Return for registering
        return ((AutoRegisterParticleType<?>) data.object()).get();
    }
}
