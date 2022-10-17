package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registration of MobEffects.
 */
public class MobEffectModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MobEffect.class, MobEffectModuleForge::register);
    }

    private static void register(RegistryEvent.Register<MobEffect> event) {
        // Register biomes
        AutoRegistrationManager.MOB_EFFECTS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerMobEffect(data, event.getRegistry()));
    }

    private static void registerMobEffect(AutoRegisterField data, IForgeRegistry<MobEffect> registry) {
        AutoRegisterMobEffect autoRegisterMobEffect = (AutoRegisterMobEffect) data.object();
        MobEffect mobEffect = autoRegisterMobEffect.get();
        mobEffect.setRegistryName(data.name());
        registry.register(mobEffect);
        data.markProcessed();
    }
}
