package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Registration of Items.
 * Provides additional static utility method for registering BlockItems.
 */
public class ItemModuleFabric {

    public static void processEntries() {
        AutoRegistrationManager.ITEMS.stream()
                .filter(data -> !data.processed())
                .forEach(ItemModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterItem autoRegisterItem = (AutoRegisterItem) data.object();
        Item item = autoRegisterItem.get();

        // Register item
        Registry.register(Registry.ITEM, data.name(), item);
        data.markProcessed();
    }

    public static void registerBlockItem(ResourceLocation resourceLocation, Block block, Item.Properties itemProperties) {
        Registry.register(Registry.ITEM, resourceLocation, new BlockItem(block, itemProperties));
    }
}