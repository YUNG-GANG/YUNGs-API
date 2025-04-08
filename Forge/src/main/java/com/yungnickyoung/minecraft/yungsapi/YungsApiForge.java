package com.yungnickyoung.minecraft.yungsapi;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod(YungsApiCommon.MOD_ID)
public class YungsApiForge {
    public static IEventBus loadingContextEventBus;
    public YungsApiForge(FMLJavaModLoadingContext context) {
        loadingContextEventBus = context.getModEventBus();
        YungsApiCommon.init();
    }

    public static <T> Consumer<RegisterEvent> buildSimpleRegistrar(
            final ResourceKey<Registry<T>> registryKey,
            final List<AutoRegisterField> registerables
    ) {
        return buildAutoRegistrar(registryKey, registerables, data -> (T) data.object());
    }

    @NotNull
    public static <T> Consumer<RegisterEvent> buildAutoRegistrar(
            final ResourceKey<Registry<T>> registryKey,
            final List<AutoRegisterField> registerables,
            final Function<AutoRegisterField, T> unwrapper
    ) {
        return event -> event.register(registryKey, helper -> registerables.stream()
                .filter(data -> !data.processed())
                .forEach(data -> {
                    helper.register(data.name(), unwrapper.apply(data));
                    data.markProcessed();
                }));
    }
}
