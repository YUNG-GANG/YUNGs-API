package com.yungnickyoung.minecraft.yungsapi.world.banner;

/**
 * Represents a single banner pattern.
 * A banner pattern consists of a String for the name of the pattern
 * and an integer color value.
 */
public class BannerPattern {
    private String pattern;
    private int color;

    public BannerPattern(String pattern, int color) {
        this.pattern = pattern;
        this.color = color;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

