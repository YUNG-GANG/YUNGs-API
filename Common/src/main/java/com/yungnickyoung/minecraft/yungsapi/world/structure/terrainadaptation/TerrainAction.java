package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an action to take when adapting terrain around a structure piece.
 * This is used to modify the noise density at the position of the terrain action.
 */
public enum TerrainAction implements StringRepresentable {
    CARVE("carve", -1),
    BURY("bury", 1),
    NONE("none", 0);

    public static final Codec<TerrainAction> CODEC = StringRepresentable.fromValues(TerrainAction::values);
    private final String name;

    /**
     * The modifier to apply to the noise density at the position of the terrain action.
     * This is a simple sign multiplier to the noise density.
     * Carving uses -1 to reduce the density, burying uses +1 to increase the density, and none uses 0 to keep the density the same.
     */
    private final int densityModifier;

    TerrainAction(String name, int densityModifier) {
        this.name = name;
        this.densityModifier = densityModifier;
    }

    public int getDensityModifier() {
        return densityModifier;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
