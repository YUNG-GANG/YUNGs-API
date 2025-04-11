package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registration of EntityDataSerializers.
 */
public class EntityDataSerializerModuleForge {
    public static void processEntries() {
        YungsApiForge.loadingContextEventBus.addListener(YungsApiForge.buildSimpleRegistrar(
                ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, AutoRegistrationManager.ENTITY_DATA_SERIALIZERS));
    }
}
