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
    private final Either<AutoRegisterConfiguredFeature<?>, Holder<ConfiguredFeature<?, ?>>> innerConfiguredFeature;
    private final List<PlacementModifier> placementModifiers;

    /*
     * On Forge, we register PlacedFeatures during CommonSetup by default.
     * However, sometimes they are needed earlier, such as during biome registration, which takes place before CommonSetup.
     *
     * To accomplish this, we allow for on-demand registration.
     * If a PlacedFeature's Holder is requested (i.e. with AutoRegisterPlacedFeature#holder), it will be registered
     * at that moment if it has not yet already been registered.
     *
     * The following variables are all internal variables necessary for this lazy-registration system.
     */
    private Holder<PlacedFeature> holder;
    private boolean registered = false;
    public ResourceLocation id = null;

    public static AutoRegisterPlacedFeature of(AutoRegisterConfiguredFeature<?> configuredFeature, List<PlacementModifier> placementModifiers) {
        return new AutoRegisterPlacedFeature(configuredFeature, placementModifiers);
    }

    public static AutoRegisterPlacedFeature of(Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder, List<PlacementModifier> placementModifiers) {
        return new AutoRegisterPlacedFeature(configuredFeatureHolder, placementModifiers);
    }

    private AutoRegisterPlacedFeature(AutoRegisterConfiguredFeature<?> autoRegisterConfiguredFeature, List<PlacementModifier> placementModifiers) {
        super(null);
        this.innerConfiguredFeature = Either.left(autoRegisterConfiguredFeature);
        this.placementModifiers = placementModifiers;
    }

    private AutoRegisterPlacedFeature(Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder, List<PlacementModifier> placementModifiers) {
        super(null);
        this.innerConfiguredFeature = Either.right(configuredFeatureHolder);
        this.placementModifiers = placementModifiers;
    }

    /**
     * Fetches this ConfiguredFeature's Holder, ensuring it has been registered.
     * This is the only way you should retrieve the Holder for this ConfiguredFeature!
     */
    public Holder<PlacedFeature> holder() {
        this.register(); // Ensure we are registered before returning the holder
        return this.holder;
    }

    private Holder<ConfiguredFeature<?, ?>> getConfiguredFeatureHolder() {
        return this.innerConfiguredFeature.map(
                AutoRegisterConfiguredFeature::holder,
                holder -> holder);
    }

    /**
     * Registers this PlacedFeature if it has not already been registered.
     * Note that this object's id must have been initialized.
     * For internal use only.
     */
    public void register() {
        if (this.registered) return; // Abort if already registered

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.hackyErase(this.getConfiguredFeatureHolder()),
                List.copyOf(this.placementModifiers));
        this.holder = BuiltinRegistries.register(
                BuiltinRegistries.PLACED_FEATURE,
                this.id,
                placedFeature);
        this.setSupplier(() -> placedFeature);
        this.registered = true;
    }
}
