package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterSoundEvent;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

/**
 * Registration of Sound Events.
 */
public class SoundEventModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.SOUND_EVENTS.stream()
                .filter(data -> !data.processed())
                .forEach(SoundEventModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterSoundEvent autoRegisterSoundEvent = (AutoRegisterSoundEvent) data.object();
        SoundEvent soundEvent = new SoundEvent(data.name());
        autoRegisterSoundEvent.setSupplier(() -> soundEvent);

        // Register
        Registry.register(Registry.SOUND_EVENT, data.name(), soundEvent);
        data.markProcessed();
    }
}