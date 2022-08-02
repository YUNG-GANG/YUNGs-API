package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of Items.
 * Provides additional static utility method for registering BlockItems.
 */
public class ItemModuleForge {
    private static final Map<String, DeferredRegister<Item>> registersByModId = new HashMap<>();

    public static void init() {
        AutoRegistrationManager.ITEMS.forEach(ItemModuleForge::register);
    }

    private static void register(RegisterData data) {
        // Create & register deferred registry for current mod's items, if necessary
        String modId = data.name().getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<Item> deferredRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
            registersByModId.put(modId, deferredRegister);
        }

        AutoRegisterItem autoRegisterItem = (AutoRegisterItem) data.object();
        Supplier<Item> itemSupplier = autoRegisterItem.getSupplier();

        // Register item
        DeferredRegister<Item> deferredRegister = registersByModId.get(modId);
        RegistryObject<Item> registryObject = deferredRegister.register(data.name().getPath(), itemSupplier);

        // Update the supplier to use the RegistryObject so that it will be properly updated later on
        autoRegisterItem.setSupplier(registryObject);
    }

    public static <T extends Block> void registerBlockItem(ResourceLocation resourceLocation, RegistryObject<T> blockRegistryObject, Item.Properties itemProperties) {
        // Create & register deferred registry for current mod's items, if necessary
        String modId = resourceLocation.getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<Item> deferredRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
            registersByModId.put(modId, deferredRegister);
        }

        // Register BlockItem
        DeferredRegister<Item> deferredRegister = registersByModId.get(modId);
        deferredRegister.register(resourceLocation.getPath(), () -> new BlockItem(blockRegistryObject.get(), itemProperties));
    }
}
