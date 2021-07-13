package com.yungnickyoung.minecraft.yungsapi;

import com.yungnickyoung.minecraft.yungsapi.init.YAModCriteria;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * API for YUNG's Minecraft mods.
 * Most classes in this project are either useful data abstractions
 * or static helper classes.
 */
public class YungsApi implements ModInitializer {
    public static final String MOD_ID = "yungsapi";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        YAModCriteria.init();
    }
}
