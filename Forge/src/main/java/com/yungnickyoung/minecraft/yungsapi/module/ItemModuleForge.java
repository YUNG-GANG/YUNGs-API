package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of Items and BlockItems.
 */
public class ItemModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ItemModuleForge::registerItems);
    }

    private static void registerItems(RegistryEvent.Register<Item> event) {
        // Register BlockItems
        AutoRegistrationManager.BLOCKS.forEach(data -> {
            AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
            if (autoRegisterBlock.hasItemProperties()) {
                BlockItem blockItem = new BlockItem(autoRegisterBlock.get(), autoRegisterBlock.getItemProperties().get());
                blockItem.setRegistryName(data.name());
                event.getRegistry().register(blockItem);
            }
        });

        // Register items
        AutoRegistrationManager.ITEMS.forEach(data -> {
            AutoRegisterItem autoRegisterItem = (AutoRegisterItem) data.object();
            Item item = autoRegisterItem.get();
            item.setRegistryName(data.name());
            event.getRegistry().register(item);
        });
    }
}
