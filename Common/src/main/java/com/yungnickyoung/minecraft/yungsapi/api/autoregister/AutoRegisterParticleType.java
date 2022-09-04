package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public class AutoRegisterParticleType<T extends ParticleOptions> extends AutoRegisterEntry<ParticleType<T>> {

    public static <U extends ParticleOptions> AutoRegisterParticleType<U> of(Supplier<ParticleType<U>> particleTypeSupplier) {
        return new AutoRegisterParticleType<>(particleTypeSupplier);
    }

    private AutoRegisterParticleType(Supplier<ParticleType<T>> particleTypeSupplier) {
        super(particleTypeSupplier);
    }
}
