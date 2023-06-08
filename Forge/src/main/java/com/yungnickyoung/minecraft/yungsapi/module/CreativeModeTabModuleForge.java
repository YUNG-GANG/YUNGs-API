package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Initialization of creative mode tabs.
 */
public class CreativeModeTabModuleForge {
    /**
     * Map for caching tabs that have already initialized.
     * Prevents potential duplicate initialization and cached object instance replacement.
     */
    private static final Map<String, AutoRegisterCreativeTab> initializedTabs = new HashMap<>();

    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeModeTabModuleForge::registerTabs);
    }

    private static void registerTabs(final RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> AutoRegistrationManager.CREATIVE_MODE_TABS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerTab(data, helper)));
    }

    private static void registerTab(AutoRegisterField data, RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        // Check cache to see if we already initialized this tab.
        // TODO - remove, as I don't think this is still necessary in 1.20.
        ResourceLocation resourceLocation = data.name();
        String name = String.format("%s.%s", resourceLocation.getNamespace(), resourceLocation.getPath());
        if (initializedTabs.containsKey(name)) {
            return;
        }

        // Extract data
        AutoRegisterCreativeTab autoRegisterCreativeTab = (AutoRegisterCreativeTab) data.object();
        Supplier<ItemStack> itemStackSupplier = autoRegisterCreativeTab.getIconItemStackSupplier();

        // Create tab
        CreativeModeTab creativeModeTab = CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + name))
                .icon(itemStackSupplier)
                .displayItems((params, output) -> {
                    // TODO
                })
                .build();

        helper.register(data.name(), creativeModeTab);

        // Update supplier to retrieve tab
        autoRegisterCreativeTab.setSupplier(() -> creativeModeTab);

        // Add to cache
        initializedTabs.put(name, autoRegisterCreativeTab);
        data.markProcessed();
    }
}