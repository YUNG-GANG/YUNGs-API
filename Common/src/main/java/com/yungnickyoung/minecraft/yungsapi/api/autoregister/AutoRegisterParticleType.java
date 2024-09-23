package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public class AutoRegisterParticleType<T extends ParticleOptions> extends AutoRegisterEntry<ParticleType<T>> {

    public static <U extends ParticleOptions> AutoRegisterParticleType<U> of(Supplier<ParticleType<U>> particleTypeSupplier) {
        return new AutoRegisterParticleType<>(particleTypeSupplier);
    }

    public static AutoRegisterParticleType<SimpleParticleType> simple() {
        return new AutoRegisterParticleType<>(() -> Services.PARTICLE_HELPER.simple(false));
    }

    public static AutoRegisterParticleType<SimpleParticleType> simple(boolean alwaysSpawn) {
        return new AutoRegisterParticleType<>(() -> Services.PARTICLE_HELPER.simple(alwaysSpawn));
    }

    private AutoRegisterParticleType(Supplier<ParticleType<T>> particleTypeSupplier) {
        super(particleTypeSupplier);
    }
}
