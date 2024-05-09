package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;

/**
 * Registration of MobEffects.
 */
public class MobEffectModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildAutoRegistrar(Registries.MOB_EFFECT, AutoRegistrationManager.MOB_EFFECTS, MobEffectModuleNeoForge::buildMobEffect));
    }

    private static MobEffect buildMobEffect(AutoRegisterField data) {
        // Return for registering
        return ((AutoRegisterMobEffect) data.object()).get();
    }
}
