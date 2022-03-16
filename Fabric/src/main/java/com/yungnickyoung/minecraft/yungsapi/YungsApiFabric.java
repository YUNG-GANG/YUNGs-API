package com.yungnickyoung.minecraft.yungsapi;

import net.fabricmc.api.ModInitializer;

public class YungsApiFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YungsApiCommon.init();
    }
}
