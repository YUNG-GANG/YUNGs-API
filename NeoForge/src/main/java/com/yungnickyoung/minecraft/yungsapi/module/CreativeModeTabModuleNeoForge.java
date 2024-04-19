package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Initialization of creative mode tabs.
 */
public class CreativeModeTabModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(CreativeModeTabModuleNeoForge::registerTabs);
    }

    private static void registerTabs(final RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> AutoRegistrationManager.CREATIVE_MODE_TABS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerTab(data, helper)));
    }

    private static void registerTab(AutoRegisterField data, RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        // Extract data
        AutoRegisterCreativeTab autoRegisterCreativeTab = (AutoRegisterCreativeTab) data.object();

        // Create tab
        CreativeModeTab.Builder creativeModeTabBuilder = CreativeModeTab.builder()
                .title(autoRegisterCreativeTab.getDisplayName())
                .icon(autoRegisterCreativeTab.getIconItemStackSupplier())
                .displayItems(autoRegisterCreativeTab.getDisplayItemsGenerator())
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

        data.markProcessed();
    }
}