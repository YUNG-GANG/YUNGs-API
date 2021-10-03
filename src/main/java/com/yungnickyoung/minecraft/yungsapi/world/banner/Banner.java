package com.yungnickyoung.minecraft.yungsapi.world.banner;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.TranslatableText;

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
    private NbtCompound nbt;
    private boolean isWallBanner;

    public Banner(List<BannerPattern> patterns, BlockState state, NbtCompound nbt) {
        this.patterns = patterns;
        this.state = state;
        this.nbt = nbt;
        this.isWallBanner = this.state.getBlock() instanceof WallBannerBlock;
    }

    public Banner(List<BannerPattern> patterns, BlockState state, NbtCompound nbt, boolean isWallBanner) {
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

    public NbtCompound getNbt() {
        return nbt;
    }

    public void setNbt(NbtCompound nbt) {
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
        private TranslatableText customNameTranslate;
        private String customColor;
        private BlockState state = Blocks.BLACK_WALL_BANNER.getDefaultState();

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
            this.customNameTranslate = new TranslatableText(translatableNamePath);
            return this;
        }

        public Builder customColor(String colorString) {
            this.customColor = colorString;
            return this;
        }

        public Banner build() {
            NbtCompound nbt = createBannerNBT(patterns);
            return new Banner(patterns, state, nbt);
        }

        /**
         * Helper function that creates a complete CompoundNBT for a banner BlockState
         * with the provided patterns.
         */
        private NbtCompound createBannerNBT(List<BannerPattern> patterns) {
            NbtCompound nbt = new NbtCompound();
            NbtList patternList = new NbtList();

            // Construct list of patterns from args
            for (BannerPattern pattern : patterns) {
                NbtCompound patternNBT = new NbtCompound();
                patternNBT.putString("Pattern", pattern.getPattern());
                patternNBT.putInt("Color", pattern.getColor());
                patternList.add(patternNBT);
            }

            // Custom name and color
            if (this.customColor != null || this.customNameTranslate != null) {
                String color = this.customColor == null ? "" : String.format("\"color\":\"%s\"", this.customColor);
                String name = this.customNameTranslate == null ? "" : String.format("\"translate\":\"%s\"", this.customNameTranslate.getKey());
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
