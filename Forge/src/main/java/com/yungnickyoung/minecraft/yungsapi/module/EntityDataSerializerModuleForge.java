package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of EntityDataSerializers.
 */
public class EntityDataSerializerModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EntityDataSerializerModuleForge::register);
    }

    private static void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, helper -> AutoRegistrationManager.ENTITY_DATA_SERIALIZERS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerEntityDataSerializer(data, helper))
        );
    }

    private static void registerEntityDataSerializer(AutoRegisterField data, RegisterEvent.RegisterHelper<EntityDataSerializer<?>> helper) {
        helper.register(data.name(), (EntityDataSerializer<?>) data.object());
        data.markProcessed();
    }
}
