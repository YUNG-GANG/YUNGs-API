package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.entry.AutoRegisterEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class AutoRegisterBlock extends AutoRegisterEntry<Block> {
    private Item.Properties itemProperties;

    public static AutoRegisterBlock of(Supplier<Block> blockSupplier) {
        return new AutoRegisterBlock(blockSupplier);
    }

    private AutoRegisterBlock(Supplier<Block> blockSupplier) {
        super(blockSupplier);
    }

    public AutoRegisterBlock withItem(Item.Properties itemProperties) {
        this.itemProperties = itemProperties;
        return this;
    }

    public Item.Properties getItemProperties() {
        return itemProperties;
    }

    public boolean hasItemProperties() {
        return itemProperties != null;
    }
}
