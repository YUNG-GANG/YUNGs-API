package com.yungnickyoung.minecraft.yungsapi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(YungsApiCommon.MOD_ID)
public class YungsApiNeoForge {
    public static IEventBus loadingContextEventBus;

    public YungsApiNeoForge(IEventBus eventBus) {
        YungsApiNeoForge.loadingContextEventBus = eventBus;

        YungsApiCommon.init();
    }
}
