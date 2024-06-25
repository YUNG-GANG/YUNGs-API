package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds methods to be executed during common setup (after registration).
 */
public class PostLoadModuleNeoForge {
    public static List<Method> METHODS = new ArrayList<>();

    public static void init() {
        YungsApiNeoForge.loadingContextEventBus.addListener(PostLoadModuleNeoForge::commonSetup);
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
                } catch (NullPointerException e) {
                    String message = String.format("Attempted to invoke AutoRegister method with null object. " +
                            "Did you forget to include a 'static' modifier for method '%s'?", m.getName());
                    YungsApiCommon.LOGGER.error(message);
                    throw new RuntimeException(message);
                }
            });
        });

        // Register compostables in case any were added during annotated method execution
        // with AutoRegisterUtils#addCompostableItem
        CompostModuleNeoForge.registerCompostables();
    }
}
