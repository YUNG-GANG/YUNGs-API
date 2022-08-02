package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.entry.AutoRegisterEntry;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class AutoRegisterItem extends AutoRegisterEntry<Item> {
    public static AutoRegisterItem of(Supplier<Item> blockSupplier) {
        return new AutoRegisterItem(blockSupplier);
    }

    private AutoRegisterItem(Supplier<Item> blockSupplier) {
        super(blockSupplier);
    }
}
