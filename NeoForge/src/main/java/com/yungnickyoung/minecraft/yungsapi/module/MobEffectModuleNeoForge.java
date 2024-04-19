package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of MobEffects.
 */
public class MobEffectModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(MobEffectModuleNeoForge::register);
    }

    private static void register(RegisterEvent event) {
        event.register(Registries.MOB_EFFECT, helper -> AutoRegistrationManager.MOB_EFFECTS.stream()
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
