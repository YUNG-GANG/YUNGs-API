package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterSoundEvent;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of Sound Events.
 */
public class SoundEventModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(SoundEventModuleNeoForge::registerSoundEvents);
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