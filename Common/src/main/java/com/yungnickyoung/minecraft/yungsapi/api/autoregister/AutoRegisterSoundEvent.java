package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

/**
 * Wrapper for registering {@link SoundEvent}s with AutoRegister.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 *  @AutoRegister("music.overworld.example_music")
 *  public static AutoRegisterSoundEvent EXAMPLE_MUSIC = AutoRegisterSoundEvent.create();
 * }
 * </pre>
 */
public class AutoRegisterSoundEvent extends AutoRegisterEntry<SoundEvent> {

    public static  AutoRegisterSoundEvent create() {
        return new AutoRegisterSoundEvent(null);
    }

    private AutoRegisterSoundEvent(Supplier<SoundEvent> soundEventSupplier) {
        super(soundEventSupplier);
    }
}
