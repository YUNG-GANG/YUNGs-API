package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterParticleType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of ParticleTypes.
 */
public class ParticleTypeModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ParticleTypeModuleForge::register);
    }

    private static void register(RegisterEvent event) {
        event.register(ForgeRegistries.PARTICLE_TYPES.getRegistryKey(), helper -> AutoRegistrationManager.PARTICLE_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerParticleType(data, helper))
        );
    }

    private static void registerParticleType(AutoRegisterField data, RegisterEvent.RegisterHelper<ParticleType<?>> helper) {
        AutoRegisterParticleType<?> autoRegisterParticleType = (AutoRegisterParticleType<?>) data.object();
        ParticleType<?> particleType = autoRegisterParticleType.get();

        // Register
        helper.register(data.name(), particleType);
        data.markProcessed();
    }
}
