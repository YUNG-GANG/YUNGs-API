package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of Items and BlockItems.
 */
public class ItemModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(ItemModuleNeoForge::registerItems);
    }

    private static void registerItems(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            // Register BlockItems
            AutoRegistrationManager.BLOCKS.forEach(data -> registerBlockItem(data, helper));

            // Register BlockItems for leftover blocks that depend on other blocks.
            // These will be things like Stairs, Slabs, Fences, Walls, etc.
            BlockModuleNeoForge.EXTRA_BLOCKS.forEach(extraBlockData -> registerExtraBlockItem(extraBlockData, helper));

            // Register items
            AutoRegistrationManager.ITEMS.stream()
                    .filter(data -> !data.processed())
                    .forEach(data -> registerItem(data, helper));
        });
    }

    private static void registerBlockItem(AutoRegisterField data, RegisterEvent.RegisterHelper<Item> helper) {
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        if (autoRegisterBlock.hasItemProperties()) {
            BlockItem blockItem = new BlockItem(autoRegisterBlock.get(), autoRegisterBlock.getItemProperties().get());
            helper.register(data.name(), blockItem);
        }
    }

    private static void registerExtraBlockItem(BlockModuleNeoForge.ExtraBlockData extraBlockData, RegisterEvent.RegisterHelper<Item> helper) {
        BlockItem blockItem = new BlockItem(extraBlockData.block(), extraBlockData.itemProperties().get());
        helper.register(extraBlockData.blockRegisteredName(), blockItem);
    }

    private static void registerItem(AutoRegisterField data, RegisterEvent.RegisterHelper<Item> helper) {
        AutoRegisterItem autoRegisterItem = (AutoRegisterItem) data.object();
        Item item = autoRegisterItem.get();
        helper.register(data.name(), item);
        data.markProcessed();
    }
}