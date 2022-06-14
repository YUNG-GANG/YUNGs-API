package com.yungnickyoung.minecraft.yungsapi.world.banner;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a Banner in Minecraft.
 *
 * Includes fields for patterns, BlockState, and NBT tags.
 * Includes an internal Builder for easy Banner construction.
 */
public class Banner {
    private List<BannerPattern> patterns;
    private BlockState state;
    private CompoundTag nbt;
    private boolean isWallBanner;

    public Banner(List<BannerPattern> patterns, BlockState state, CompoundTag nbt) {
        this.patterns = patterns;
        this.state = state;
        this.nbt = nbt;
        this.isWallBanner = this.state.getBlock() instanceof WallBannerBlock;
    }

    public Banner(List<BannerPattern> patterns, BlockState state, CompoundTag nbt, boolean isWallBanner) {
        this.patterns = patterns;
        this.state = state;
        this.nbt = nbt;
        this.isWallBanner = isWallBanner;
    }

    public List<BannerPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<BannerPattern> patterns) {
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
     *
     * This makes it easy to construct banners from code and then extract the BlockState and NBT,
     * without having to manually construct a compound NBT.
     */
    public static class Builder {
        private final List<BannerPattern> patterns = new ArrayList<>();
        private String customNameTranslate;
        private String customColor;
        private BlockState state = Blocks.BLACK_WALL_BANNER.defaultBlockState();

        public Builder() {
        }

        public Builder blockState(BlockState state) {
            this.state = state;
            return this;
        }

        public Builder pattern(BannerPattern pattern) {
            patterns.add(pattern);
            return this;
        }

        public Builder pattern(String pattern, int color) {
            patterns.add(new BannerPattern(pattern, color));
            return this;
        }

        public Builder customName(String translatableNamePath) {
            this.customNameTranslate = translatableNamePath;
            return this;
        }

        public Builder customColor(String colorString) {
            this.customColor = colorString;
            return this;
        }

        public Banner build() {
            CompoundTag nbt = createBannerNBT(patterns);
            return new Banner(patterns, state, nbt);
        }

        /**
         * Helper function that creates a complete CompoundNBT for a banner BlockState
         * with the provided patterns.
         */
        private CompoundTag createBannerNBT(List<BannerPattern> patterns) {
            CompoundTag nbt = new CompoundTag();
            ListTag patternList = new ListTag();

            // Construct list of patterns from args
            for (BannerPattern pattern : patterns) {
                CompoundTag patternNBT = new CompoundTag();
                patternNBT.putString("Pattern", pattern.getPattern());
                patternNBT.putInt("Color", pattern.getColor());
                patternList.add(patternNBT);
            }

            // Custom name and color
            if (this.customColor != null || this.customNameTranslate != null) {
                String color = this.customColor == null ? "" : String.format("\"color\":\"%s\"", this.customColor);
                String name = this.customNameTranslate == null ? "" : String.format("\"translate\":\"%s\"", this.customNameTranslate);
                if (this.customColor != null && this.customNameTranslate != null) name = "," + name;
                String customNameString = "{" + color + name + "}";

                nbt.putString("CustomName", customNameString);
            }

            // Add tags to NBT
            nbt.put("Patterns", patternList);
            nbt.putString("id", "minecraft:banner");

            return nbt;
        }
    }
}
