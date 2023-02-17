package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.mojang.datafixers.util.Either;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class AutoRegisterPlacedFeature extends AutoRegisterEntry<PlacedFeature> {
    // Data fields, for the user to declare
    private final Either<AutoRegisterConfiguredFeature, Holder<ConfiguredFeature<?, ?>>> innerConfiguredFeature;
    private final List<PlacementModifier> placementModifiers;

    private Holder<PlacedFeature> holder;
    private boolean registered = false;
    public ResourceLocation id = null;

    public static AutoRegisterPlacedFeature of(AutoRegisterConfiguredFeature configuredFeature, List<PlacementModifier> placementModifiers) {
        return new AutoRegisterPlacedFeature(configuredFeature, placementModifiers);
    }

    public static AutoRegisterPlacedFeature of(Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder, List<PlacementModifier> placementModifiers) {
        return new AutoRegisterPlacedFeature(configuredFeatureHolder, placementModifiers);
    }

    private AutoRegisterPlacedFeature(AutoRegisterConfiguredFeature autoRegisterConfiguredFeature, List<PlacementModifier> placementModifiers) {
        super(null);
        this.innerConfiguredFeature = Either.left(autoRegisterConfiguredFeature);
        this.placementModifiers = placementModifiers;
    }

    private AutoRegisterPlacedFeature(Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder, List<PlacementModifier> placementModifiers) {
        super(null);
        this.innerConfiguredFeature = Either.right(configuredFeatureHolder);
        this.placementModifiers = placementModifiers;
    }

    public Holder<PlacedFeature> holder() {
        this.register(); // Ensure we are registered before returning the holder
        return this.holder;
    }

    public Holder<ConfiguredFeature<?, ?>> getConfiguredFeatureHolder() {
        return this.innerConfiguredFeature.map(
                AutoRegisterConfiguredFeature::holder,
                holder -> holder);
    }

    public List<PlacementModifier> placementModifiers() {
        return this.placementModifiers;
    }

    public void setHolder(Holder<PlacedFeature> holder) {
        this.holder = holder;
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void register() {
        if (this.registered) return; // Abort if already registered

        // Ensure the configured feature has been registered so that its holder is populated
        this.innerConfiguredFeature.ifLeft(AutoRegisterConfiguredFeature::register);

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.hackyErase(this.getConfiguredFeatureHolder()),
                List.copyOf(this.placementModifiers()));
        Holder<PlacedFeature> holder = BuiltinRegistries.register(
                BuiltinRegistries.PLACED_FEATURE,
                this.id,
                placedFeature);
        this.setHolder(holder);
        this.setSupplier(() -> placedFeature);
        this.registered = true;
    }
}
