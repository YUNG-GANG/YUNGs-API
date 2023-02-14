package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Supplier;

public class AutoRegisterPlacedFeature extends AutoRegisterEntry<PlacedFeature> {
    public static AutoRegisterPlacedFeature of(Supplier<PlacedFeature> placedFeatureSupplier) {
        return new AutoRegisterPlacedFeature(placedFeatureSupplier);
    }

    private AutoRegisterPlacedFeature(Supplier<PlacedFeature> placedFeatureSupplier) {
        super(placedFeatureSupplier);
    }
}
