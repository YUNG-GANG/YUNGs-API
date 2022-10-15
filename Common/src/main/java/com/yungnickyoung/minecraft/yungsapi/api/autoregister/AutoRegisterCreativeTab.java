package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Wrapper for registering {@link CreativeModeTab}s with AutoRegister.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 * @AutoRegister("general")
 * public static AutoRegisterCreativeTab TAB_GENERAL = new AutoRegisterCreativeTab.Builder()
 *         .iconItem(() -> new ItemStack(BlockModule.ICICLE.get()))
 *         .build();
 * }
 * </pre>
 */
public class AutoRegisterCreativeTab extends AutoRegisterEntry<CreativeModeTab> {
    private final Supplier<ItemStack> iconItemStackSupplier;

    private AutoRegisterCreativeTab(Builder builder) {
        super(() -> null);
        this.iconItemStackSupplier = builder.iconItemStackSupplier;
    }

    public Supplier<ItemStack> getIconItemStackSupplier() {
        return iconItemStackSupplier;
    }

    public static class Builder {
        private Supplier<ItemStack> iconItemStackSupplier;

        public Builder() {
        }

        public Builder iconItem(Supplier<ItemStack> iconItemStack) {
            this.iconItemStackSupplier = iconItemStack;
            return this;
        }

        public AutoRegisterCreativeTab build() {
            return new AutoRegisterCreativeTab(this);
        }

        public Supplier<ItemStack> getIconItemStackSupplier() {
            return iconItemStackSupplier;
        }
    }
}