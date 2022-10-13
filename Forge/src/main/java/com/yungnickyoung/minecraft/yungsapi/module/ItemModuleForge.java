package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of Items and BlockItems.
 */
public class ItemModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemModuleForge::registerItems);
    }

    private static void registerItems(RegisterEvent event) {
        event.register(Registry.ITEM_REGISTRY, helper -> {
            // Register BlockItems
            AutoRegistrationManager.BLOCKS.forEach(data -> registerBlockItem(data, helper));

            // Register items
            AutoRegistrationManager.ITEMS.stream()
                    .filter(data -> !data.processed())
                    .forEach(data -> registerItem(data, helper));
        });
    }

    private static void registerBlockItem(RegisterData data, RegisterEvent.RegisterHelper<Item> helper) {
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        if (autoRegisterBlock.hasItemProperties()) {
            BlockItem blockItem = new BlockItem(autoRegisterBlock.get(), autoRegisterBlock.getItemProperties().get());
            helper.register(data.name(), blockItem);
        }
    }

    private static void registerItem(RegisterData data, RegisterEvent.RegisterHelper<Item> helper) {
        AutoRegisterItem autoRegisterItem = (AutoRegisterItem) data.object();
        Item item = autoRegisterItem.get();
        helper.register(data.name(), item);
        data.markProcessed();
    }
}