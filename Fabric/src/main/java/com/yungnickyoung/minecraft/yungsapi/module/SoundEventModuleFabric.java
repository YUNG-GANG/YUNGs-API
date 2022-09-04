package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterSoundEvent;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

/**
 * Registration of SoundEvents.
 */
public class SoundEventModuleFabric {
    public static void init() {
        AutoRegistrationManager.SOUND_EVENTS.forEach(SoundEventModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterSoundEvent autoRegisterSoundEvent = (AutoRegisterSoundEvent) data.object();
        SoundEvent soundEvent = new SoundEvent(data.name());
        autoRegisterSoundEvent.setSupplier(() -> soundEvent);

        // Register
        Registry.register(Registry.SOUND_EVENT, data.name(), soundEvent);
    }
}
