package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class AutoRegisterBlock extends AutoRegisterEntry<Block> {
    private Supplier<Item.Properties> itemProperties;
    private boolean hasStairs;
    private boolean hasSlab;
    private boolean hasFence;
    private boolean hasFenceGate;
    private boolean hasWall;

    public static AutoRegisterBlock of(Supplier<Block> blockSupplier) {
        return new AutoRegisterBlock(blockSupplier);
    }

    private AutoRegisterBlock(Supplier<Block> blockSupplier) {
        super(blockSupplier);
    }

    public AutoRegisterBlock withItem(Supplier<Item.Properties> itemProperties) {
        this.itemProperties = itemProperties;
        return this;
    }

    public AutoRegisterBlock withStairs() {
        this.hasStairs = true;
        return this;
    }

    public AutoRegisterBlock withSlab() {
        this.hasSlab = true;
        return this;
    }

    public AutoRegisterBlock withFence() {
        this.hasFence = true;
        return this;
    }

    public AutoRegisterBlock withFenceGate() {
        this.hasFenceGate = true;
        return this;
    }

    public AutoRegisterBlock withWall() {
        this.hasWall = true;
        return this;
    }

    public Supplier<Item.Properties> getItemProperties() {
        return itemProperties;
    }

    public boolean hasItemProperties() {
        return itemProperties != null;
    }

    public boolean hasStairs() {
        return hasStairs;
    }

    public boolean hasSlab() {
        return hasSlab;
    }

    public boolean hasFence() {
        return hasFence;
    }

    public boolean hasFenceGate() {
        return hasFenceGate;
    }

    public boolean hasWall() {
        return hasWall;
    }
}
