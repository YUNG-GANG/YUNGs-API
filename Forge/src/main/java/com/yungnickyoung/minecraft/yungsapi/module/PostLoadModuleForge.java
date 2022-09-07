package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds methods for executing during common setup (after registration).
 */
public class PostLoadModuleForge {
    public static List<Method> METHODS = new ArrayList<>();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PostLoadModuleForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            METHODS.forEach(m -> {
                // Invoke method
                try {
                    m.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    YungsApiCommon.LOGGER.error("Unable to invoke AutoRegister method {}", m.getName());
                    YungsApiCommon.LOGGER.error("Make sure the method is static and has no parameters!");
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
