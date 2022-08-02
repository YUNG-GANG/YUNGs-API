package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class AutoRegisterCreativeTabBuilder {
    private final ResourceLocation tabId;
    private Supplier<ItemStack> iconItemStackSupplier;

    public AutoRegisterCreativeTabBuilder(ResourceLocation tabId) {
        this.tabId = tabId;
    }

    public AutoRegisterCreativeTabBuilder iconItem(Supplier<ItemStack> iconItemStack) {
        this.iconItemStackSupplier = iconItemStack;
        return this;
    }

    public CreativeModeTab build() {
        return Services.CREATIVE_MODE_TAB.buildCreativeModeTab(this);
    }

    public ResourceLocation getTabId() {
        return tabId;
    }

    public Supplier<ItemStack> getIconItemStackSupplier() {
        return iconItemStackSupplier;
    }
}
