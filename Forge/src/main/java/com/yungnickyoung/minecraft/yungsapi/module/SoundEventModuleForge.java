package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterSoundEvent;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of Sound Events.
 */
public class SoundEventModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SoundEventModuleForge::registerSoundEvents);
    }

    private static void registerSoundEvents(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, helper -> AutoRegistrationManager.SOUND_EVENTS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerSoundEvent(data, helper)));
    }

    private static void registerSoundEvent(AutoRegisterField data, RegisterEvent.RegisterHelper<SoundEvent> helper) {
        AutoRegisterSoundEvent autoRegisterSoundEvent = (AutoRegisterSoundEvent) data.object();
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(data.name());
        helper.register(data.name(), soundEvent);
        autoRegisterSoundEvent.setSupplier(() -> soundEvent);
        data.markProcessed();
    }
}