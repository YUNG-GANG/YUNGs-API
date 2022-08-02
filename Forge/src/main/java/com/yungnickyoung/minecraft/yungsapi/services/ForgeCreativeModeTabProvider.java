package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTabBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ForgeCreativeModeTabProvider implements ICreativeModeTabProvider {
    @Override
    public CreativeModeTab buildCreativeModeTab(AutoRegisterCreativeTabBuilder builder) {
        return new CreativeModeTab(String.format("%s.%s", builder.getTabId().getNamespace(), builder.getTabId().getPath())) {
            @Override
            public ItemStack makeIcon() {
                return builder.getIconItemStackSupplier().get();
            }
        };
    }
}
