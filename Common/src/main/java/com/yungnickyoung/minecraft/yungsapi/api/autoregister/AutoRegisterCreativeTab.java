package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.network.chat.Component;
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
 * public static AutoRegisterCreativeTab TAB_GENERAL = AutoRegisterCreativeTab.builder()
 *         .title(Component.literal("My Mod Tab"))
 *         .iconItem(() -> new ItemStack(BlockModule.ICICLE.get()))
 *         .entries((params, output) -> { ... })
 *         .build();
 * }
 * </pre>
 */
public class AutoRegisterCreativeTab extends AutoRegisterEntry<CreativeModeTab> {
    private final Component displayName;
    private final Supplier<ItemStack> iconGenerator;
    private final CreativeModeTab.DisplayItemsGenerator displayItemsGenerator;
    private final boolean canScroll;
    private final boolean showTitle;
    private final boolean alignedRight;
    private final CreativeModeTab.Type type;
    private final String backgroundSuffix;

    /**
     * Private constructor. Use {@link AutoRegisterCreativeTab.Builder} to create instances.
     * @param builder Builder instance
     */
    private AutoRegisterCreativeTab(Builder builder) {
        super(() -> null);
        this.displayName = builder.displayName;
        this.iconGenerator = builder.iconGenerator;
        this.displayItemsGenerator = builder.displayItemsGenerator;
        this.canScroll = builder.canScroll;
        this.showTitle = builder.showTitle;
        this.alignedRight = builder.alignedRight;
        this.type = builder.type;
        this.backgroundSuffix = builder.backgroundSuffix;
    }

    /**
     * Builder for {@link AutoRegisterCreativeTab}s.
     * @return Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    public Component getDisplayName() {
        return displayName;
    }

    public Supplier<ItemStack> getIconItemStackSupplier() {
        return iconGenerator;
    }

    public CreativeModeTab.DisplayItemsGenerator getDisplayItemsGenerator() {
        return displayItemsGenerator;
    }

    public boolean canScroll() {
        return canScroll;
    }

    public boolean showTitle() {
        return showTitle;
    }

    public boolean alignedRight() {
        return alignedRight;
    }

    public CreativeModeTab.Type getType() {
        return type;
    }

    public String getBackgroundSuffix() {
        return backgroundSuffix;
    }

    /**
     * Builder for {@link AutoRegisterCreativeTab}s.
     */
    public static class Builder {
        private Component displayName = Component.empty();
        private Supplier<ItemStack> iconGenerator = () -> ItemStack.EMPTY;
        private CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = (itemDisplayParameters, output) -> {};
        private boolean canScroll = true;
        private boolean showTitle = true;
        private boolean alignedRight = false;
        private final CreativeModeTab.Type type = CreativeModeTab.Type.CATEGORY;
        private String backgroundSuffix = "items.png";

        private Builder() {
        }

        public Builder iconItem(Supplier<ItemStack> iconItemStack) {
            this.iconGenerator = iconItemStack;
            return this;
        }

        public Builder title(Component title) {
            this.displayName = title;
            return this;
        }

        public Builder entries(CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
            this.displayItemsGenerator = displayItemsGenerator;
            return this;
        }

        public Builder alignedRight() {
            this.alignedRight = true;
            return this;
        }

        public Builder hideTitle() {
            this.showTitle = false;
            return this;
        }

        public Builder noScrollBar() {
            this.canScroll = false;
            return this;
        }

        public Builder backgroundSuffix(String string) {
            this.backgroundSuffix = string;
            return this;
        }

        public AutoRegisterCreativeTab build() {
            return new AutoRegisterCreativeTab(this);
        }
    }
}