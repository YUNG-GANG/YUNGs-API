package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;

/**
 * Registration of MobEffects.
 */
public class MobEffectModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.MOB_EFFECTS.stream()
                .filter(data -> !data.processed())
                .forEach(MobEffectModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterMobEffect autoRegisterMobEffect = (AutoRegisterMobEffect) data.object();
        MobEffect mobEffect = autoRegisterMobEffect.get();

        // Register mob effect
        Registry.register(Registry.MOB_EFFECT, data.name(), mobEffect);
        data.markProcessed();
    }
}
