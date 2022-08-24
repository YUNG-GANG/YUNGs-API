package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of Items and BlockItems.
 */
public class ItemModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemModuleForge::registerItems);
    }

    private static void registerItems(RegisterEvent event) {
        event.register(Registry.ITEM_REGISTRY, helper -> {
            // Register BlockItems
            AutoRegistrationManager.BLOCKS.forEach(data -> {
                AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
                if (autoRegisterBlock.hasItemProperties()) {
                    BlockItem blockItem = new BlockItem(autoRegisterBlock.get(), autoRegisterBlock.getItemProperties().get());
                    helper.register(data.name(), blockItem);
                }
            });

            // Register items
            AutoRegistrationManager.ITEMS.forEach(data -> {
                AutoRegisterItem autoRegisterItem = (AutoRegisterItem) data.object();
                Item item = autoRegisterItem.get();
                helper.register(data.name(), item);
            });
        });
    }
}