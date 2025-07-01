package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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

        // We directly reference the registry instead of using the helper so we can set the holder on the AutoRegisterMobEffect instance.
        // At the time of writing, the helper does not provide a way to get the holder after registration.
        Holder<MobEffect> holder = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, data.name(), mobEffect);
        ((AutoRegisterMobEffect) data.object()).setHolder(holder);

        data.markProcessed();
    }
}
