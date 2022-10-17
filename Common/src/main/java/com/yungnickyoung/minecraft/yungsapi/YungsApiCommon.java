package com.yungnickyoung.minecraft.yungsapi;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * API for YUNG's Minecraft mods.
 * Most classes in this project are either useful data abstractions
 * or static helper classes.
 */
public class YungsApiCommon {
    public static final String MOD_ID = "yungsapi";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        AutoRegistrationManager.initAutoRegPackage("com.yungnickyoung.minecraft.yungsapi.module");
    }
}
