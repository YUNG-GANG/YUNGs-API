package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeModeTabModuleForge::initializeTabs);
    }

    private static void initializeTabs(final CreativeModeTabEvent.Register event) {
        AutoRegistrationManager.CREATIVE_MODE_TABS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerTab(data, event));
    }

    private static void registerTab(AutoRegisterField data, final CreativeModeTabEvent.Register event) {
        // Check cache to see if we already initialized this tab.
        // Not sure if caching is still necessary in 1.19.3.
        ResourceLocation resourceLocation = data.name();
        String name = String.format("%s.%s", resourceLocation.getNamespace(), resourceLocation.getPath());
        if (initializedTabs.containsKey(name)) {
            return;
        }

        // Extract data
        AutoRegisterCreativeTab autoRegisterCreativeTab = (AutoRegisterCreativeTab) data.object();
        Supplier<ItemStack> itemStackSupplier = autoRegisterCreativeTab.getIconItemStackSupplier();

        // Create tab
        CreativeModeTab creativeModeTab = event.registerCreativeModeTab(data.name(), builder -> builder
                .title(Component.translatable("itemGroup." + name))
                .icon(itemStackSupplier)
                .displayItems((enabledFlags, populator) -> {
                    // TODO
                })
                .build()
        );

        // Update supplier to retrieve tab
        autoRegisterCreativeTab.setSupplier(() -> creativeModeTab);

        // Add to cache
        initializedTabs.put(name, autoRegisterCreativeTab);
        data.markProcessed();
    }
}