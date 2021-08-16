package com.yungnickyoung.minecraft.yungsapi;

import com.yungnickyoung.minecraft.yungsapi.init.YAModCriteria;
//import com.yungnickyoung.minecraft.yungsapi.init.YAModWorldgen;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * API for YUNG's Minecraft mods.
 * Most classes in this project are either useful data abstractions
 * or static helper classes.
 */
@Mod(YungsApi.MOD_ID)
public class YungsApi {
    public static final String MOD_ID = "yungsapi";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public YungsApi() {
//        YAModJigsaw.init();
        YAModCriteria.init();
//        YAModWorldgen.init();
    }
}
