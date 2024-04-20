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
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildAutoRegistrar(Registries.SOUND_EVENT, AutoRegistrationManager.SOUND_EVENTS, SoundEventModuleNeoForge::buildSoundEvent));
    }

    private static SoundEvent buildSoundEvent(AutoRegisterField data) {
        AutoRegisterSoundEvent autoRegisterSoundEvent = (AutoRegisterSoundEvent) data.object();
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(data.name());
        autoRegisterSoundEvent.setSupplier(() -> soundEvent);

        // Return for registering
        return soundEvent;
    }
}