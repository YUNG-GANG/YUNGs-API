package com.yungnickyoung.minecraft.yungsapi.world.banner;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom representation of a Banner.
 * Includes fields for patterns, BlockState, and NBT tags.
 * Includes a public Builder for easy Banner construction.
 * Useful for processing banners in structures during worldgen.
 */
public class Banner {
    private List<ColoredBannerPattern> patterns;
    private BlockState state;
    private CompoundTag nbt;
    private boolean isWallBanner;

    public Banner(List<ColoredBannerPattern> patterns, BlockState state, CompoundTag nbt) {
        this.patterns = patterns;
        this.state = state;
        this.nbt = nbt;
        this.isWallBanner = this.state.getBlock() instanceof WallBannerBlock;
    }

    public Banner(List<ColoredBannerPattern> patterns, BlockState state, CompoundTag nbt, boolean isWallBanner) {
        this.patterns = patterns;
        this.state = state;
        this.nbt = nbt;
        this.isWallBanner = isWallBanner;
    }

    public List<ColoredBannerPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<ColoredBannerPattern> patterns) {
        this.patterns = patterns;
    }

    public BlockState getState() {
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public CompoundTag getNbt() {
        return nbt;
    }

    public void setNbt(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public boolean isWallBanner() {
        return isWallBanner;
    }

    public void setWallBanner(boolean wallBanner) {
        isWallBanner = wallBanner;
    }

    /**
     * Builder class for Banners.
     * <p>
     * This makes it easy to construct banners from code and then extract the BlockState and NBT,
     * without having to manually construct a compound NBT.
     */
    public static class Builder {
        private final List<ColoredBannerPattern> patterns = new ArrayList<>();
        private String     customNameTranslate;
        private TextColor  customColor;
        private BlockState state = Blocks.BLACK_WALL_BANNER.defaultBlockState();

        public Builder() {
        }

        public Builder blockState(BlockState state) {
            this.state = state;
            return this;
        }

        public Builder pattern(ColoredBannerPattern pattern) {
            patterns.add(pattern);
            return this;
        }

        public Builder pattern(ResourceKey<BannerPattern> pattern, DyeColor color) {
            patterns.add(new ColoredBannerPattern(pattern, color));
            return this;
        }

        public Builder customName(String translatableNamePath) {
            this.customNameTranslate = translatableNamePath;
            return this;
        }

        public Builder customColor(String colorString) {
            this.customColor = TextColor.parseColor(colorString).getOrThrow();
            return this;
        }

        public Builder customColor(TextColor textColor) {
            this.customColor = textColor;
            return this;
        }

        public Banner build() {
            CompoundTag nbt = createBannerNBT();
            return new Banner(patterns, state, nbt);
        }

        /**
         * Helper function that creates a complete CompoundNBT for a banner BlockState
         * with the provided patterns.
         */
        private CompoundTag createBannerNBT() {
            CompoundTag nbt = new CompoundTag();
            ListTag patternList = new ListTag();

            // Construct list of patterns from args
            patterns.forEach(pattern -> {
                CompoundTag patternNBT = new CompoundTag();
                patternNBT.putString("pattern", pattern.getPattern().identifier().toString());
                patternNBT.putString("color", pattern.getColor().getName());
                patternList.add(patternNBT);
            });

            // Custom name and color
            if (this.customColor != null || this.customNameTranslate != null) {
                var components = DataComponentMap.builder()
                        .set(DataComponents.CUSTOM_NAME, Component.translatable(this.customNameTranslate).withStyle(s -> this.customColor == null ? s : s.withColor(this.customColor)))
                        .build();
                nbt.store("components", DataComponentMap.CODEC, components);
            }

            // Add tags to NBT
            nbt.put("patterns", patternList);
            nbt.putString("id", "minecraft:banner");

            return nbt;
        }
    }
}
