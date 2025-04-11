package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

/**
 * Registration of EntityDataSerializers.
 */
public class EntityDataSerializerModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.ENTITY_DATA_SERIALIZERS.stream()
                .filter(data -> !data.processed())
                .forEach(EntityDataSerializerModuleFabric::register);
    }

    @SuppressWarnings("all")
    private static void register(AutoRegisterField data) {
        EntityDataSerializers.registerSerializer((EntityDataSerializer<?>) data.object());
        data.markProcessed();
    }
}