package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registration of EntityDataSerializers.
 */
public class EntityDataSerializerModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildSimpleRegistrar(
                NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, AutoRegistrationManager.ENTITY_DATA_SERIALIZERS));
    }
}
