package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Initialization of creative mode tabs.
 */
public class CreativeModeTabModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.CREATIVE_MODE_TABS.stream()
                .filter(data -> !data.processed())
                .forEach(CreativeModeTabModuleFabric::initialize);
    }

    private static void initialize(AutoRegisterField data) {
        // Extract data
        AutoRegisterCreativeTab autoRegisterCreativeTab = (AutoRegisterCreativeTab) data.object();

        // Create tab
        CreativeModeTab.Builder creativeModeTabBuilder = FabricItemGroup.builder()
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
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, data.name(), creativeModeTab);

        // Update supplier to retrieve tab
        autoRegisterCreativeTab.setSupplier(() -> creativeModeTab);

        data.markProcessed();
    }
}