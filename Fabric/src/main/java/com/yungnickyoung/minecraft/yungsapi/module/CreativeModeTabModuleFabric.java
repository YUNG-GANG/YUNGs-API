package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Initialization of creative mode tabs.
 */
public class CreativeModeTabModuleFabric {
    public static void init() {
        AutoRegistrationManager.CREATIVE_MODE_TABS.forEach(CreativeModeTabModuleFabric::initialize);
    }

    private static void initialize(RegisterData data) {
        // Extract data
        AutoRegisterCreativeTab autoRegisterCreativeTab = (AutoRegisterCreativeTab) data.object();
        ResourceLocation name = data.name();
        Supplier<ItemStack> itemStackSupplier = autoRegisterCreativeTab.getIconItemStackSupplier();

        // Create tab
        CreativeModeTab creativeModeTab = FabricItemGroupBuilder.build(name, itemStackSupplier);

        // Update supplier to retrieve tab
        autoRegisterCreativeTab.setSupplier(() -> creativeModeTab);
    }
}