package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of MobEffects.
 */
public class MobEffectModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(MobEffectModuleForge::register);
    }

    private static void register(RegisterEvent event) {
        event.register(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), helper -> AutoRegistrationManager.MOB_EFFECTS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerMobEffect(data, helper))
        );
    }

    private static void registerMobEffect(AutoRegisterField data, RegisterEvent.RegisterHelper<MobEffect> helper) {
        AutoRegisterMobEffect autoRegisterMobEffect = (AutoRegisterMobEffect) data.object();
        MobEffect mobEffect = autoRegisterMobEffect.get();
        helper.register(data.name(), mobEffect);
        data.markProcessed();
    }
}
