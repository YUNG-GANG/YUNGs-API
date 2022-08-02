package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTabBuilder;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.world.item.CreativeModeTab;

public class FabricCreativeModeTabProvider implements ICreativeModeTabProvider {
    @Override
    public CreativeModeTab buildCreativeModeTab(AutoRegisterCreativeTabBuilder builder) {
        return FabricItemGroupBuilder.build(
                builder.getTabId(),
                builder.getIconItemStackSupplier()
        );
    }
}
