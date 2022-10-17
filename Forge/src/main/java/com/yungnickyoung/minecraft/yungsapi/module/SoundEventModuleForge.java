package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterSoundEvent;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Registration of SoundEvents.
 */
public class SoundEventModuleForge {
    private static final Map<String, DeferredRegister<SoundEvent>> registersByModId = new HashMap<>();

    public static void processEntries() {
        AutoRegistrationManager.SOUND_EVENTS.stream()
                .filter(data -> !data.processed())
                .forEach(SoundEventModuleForge::register);
    }

    private static void register(AutoRegisterField data) {
        // Create & register deferred registry for current mod, if necessary
        String modId = data.name().getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<SoundEvent> deferredRegister = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, modId);
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
            registersByModId.put(modId, deferredRegister);
        }

        AutoRegisterSoundEvent autoRegisterSoundEvent = (AutoRegisterSoundEvent) data.object();
        SoundEvent soundEvent = new SoundEvent(data.name());

        // Register
        DeferredRegister<SoundEvent> deferredRegister = registersByModId.get(modId);
        RegistryObject<SoundEvent> registryObject = deferredRegister.register(data.name().getPath(), () -> soundEvent);

        // Update the supplier to use the RegistryObject so that it will be properly updated later on
        autoRegisterSoundEvent.setSupplier(registryObject);

        data.markProcessed();
    }
}
