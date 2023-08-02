package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

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
        // TODO - remove, as I don't think this is still necessary in 1.20+
        String name = String.format("%s.%s", data.name().getNamespace(), data.name().getPath());
        if (initializedTabs.containsKey(name)) {
            return;
        }

        // Extract data
        AutoRegisterCreativeTab autoRegisterCreativeTab = (AutoRegisterCreativeTab) data.object();

        // Create tab
        CreativeModeTab.Builder creativeModeTabBuilder = CreativeModeTab.builder()
                .title(autoRegisterCreativeTab.getDisplayName())
                .icon(autoRegisterCreativeTab.getIconItemStackSupplier())
                .displayItems((params, output) -> autoRegisterCreativeTab.getDisplayItemsGenerator().accept(params, output))
                .backgroundSuffix(autoRegisterCreativeTab.getBackgroundSuffix());
        if (!autoRegisterCreativeTab.canScroll()) {
            creativeModeTabBuilder.noScrollBar();
        }
        if (!autoRegisterCreativeTab.showTitle()) {
            creativeModeTabBuilder.hideTitle();
        }
        if (autoRegisterCreativeTab.alignedRight()) {
            creativeModeTabBuilder.alignedRight();
        }

        CreativeModeTab creativeModeTab = creativeModeTabBuilder.build();

        // Register tab
        helper.register(data.name(), creativeModeTab);

        // Update supplier to retrieve tab
        autoRegisterCreativeTab.setSupplier(() -> creativeModeTab);

        // Add to cache
        initializedTabs.put(name, autoRegisterCreativeTab);
        data.markProcessed();
    }
}