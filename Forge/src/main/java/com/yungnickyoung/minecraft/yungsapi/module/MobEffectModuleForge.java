package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of MobEffects.
 */
public class MobEffectModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MobEffect.class, MobEffectModuleForge::register);
    }

    private static void register(RegistryEvent.Register<MobEffect> event) {
        // Register biomes
        AutoRegistrationManager.MOB_EFFECTS.forEach(data -> {
            AutoRegisterMobEffect autoRegisterMobEffect = (AutoRegisterMobEffect) data.object();
            MobEffect mobEffect = autoRegisterMobEffect.get();

            // Register
            mobEffect.setRegistryName(data.name());
            event.getRegistry().register(mobEffect);
        });
    }
}
