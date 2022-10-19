package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registration of Items and BlockItems.
 */
public class ItemModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ItemModuleForge::register);
    }

    private static void register(RegistryEvent.Register<Item> event) {
        // Register BlockItems
        AutoRegistrationManager.BLOCKS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerBlockItem(data, event.getRegistry()));

        // Register BlockItems for leftover blocks that depend on other blocks.
        // These will be things like Stairs, Slabs, Fences, Walls, etc.
        BlockModuleForge.EXTRA_BLOCKS.forEach(extraBlockData -> registerExtraBlockItem(extraBlockData, event.getRegistry()));

        // Register items
        AutoRegistrationManager.ITEMS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerItem(data, event.getRegistry()));
    }

    private static void registerBlockItem(AutoRegisterField data, IForgeRegistry<Item> registry) {
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        if (autoRegisterBlock.hasItemProperties()) {
            BlockItem blockItem = new BlockItem(autoRegisterBlock.get(), autoRegisterBlock.getItemProperties().get());
            blockItem.setRegistryName(data.name());
            registry.register(blockItem);
            data.markProcessed();
        }
    }

    private static void registerExtraBlockItem(BlockModuleForge.ExtraBlockData extraBlockData, IForgeRegistry<Item> registry) {
        BlockItem blockItem = new BlockItem(extraBlockData.block(), extraBlockData.itemProperties().get());
        blockItem.setRegistryName(extraBlockData.block().getRegistryName());
        registry.register(blockItem);
    }

    private static void registerItem(AutoRegisterField data, IForgeRegistry<Item> registry) {
        AutoRegisterItem autoRegisterItem = (AutoRegisterItem) data.object();
        Item item = autoRegisterItem.get();
        item.setRegistryName(data.name());
        registry.register(item);
        data.markProcessed();
    }
}
