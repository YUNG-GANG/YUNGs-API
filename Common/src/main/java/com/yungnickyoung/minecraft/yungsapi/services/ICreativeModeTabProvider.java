package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTabBuilder;
import net.minecraft.world.item.CreativeModeTab;

public interface ICreativeModeTabProvider {
    CreativeModeTab buildCreativeModeTab(AutoRegisterCreativeTabBuilder builder);
}
