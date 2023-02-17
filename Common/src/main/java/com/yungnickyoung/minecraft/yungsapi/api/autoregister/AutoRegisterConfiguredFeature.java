package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.function.Supplier;

public class AutoRegisterConfiguredFeature<FC extends FeatureConfiguration> extends AutoRegisterEntry<ConfiguredFeature<FC, Feature<FC>>> {
    private final Feature<FC> feature;
    private final Supplier<FC> featureConfiguration;

    /*
     * On Forge, we register ConfiguredFeatures during CommonSetup by default.
     * However, sometimes they are needed earlier, such as during biome registration, which takes place before CommonSetup.
     *
     * To accomplish this, we allow for on-demand registration.
     * If a ConfiguredFeature's Holder is requested (i.e. with AutoRegisterConfiguredFeature#holder), it will be registered
     * at that moment if it has not yet already been registered.
     *
     * The following variables are all internal variables necessary for this lazy-registration system.
     */
    private Holder<ConfiguredFeature<?, ?>> holder;
    private boolean registered = false;
    public ResourceLocation id = null;

    public static <FC extends FeatureConfiguration> AutoRegisterConfiguredFeature<FC> of(Feature<FC> feature, Supplier<FC> featureConfiguration) {
        return new AutoRegisterConfiguredFeature<>(feature, featureConfiguration);
    }

    private AutoRegisterConfiguredFeature(Feature<FC> feature, Supplier<FC> featureConfiguration) {
        super(null);
        this.feature = feature;
        this.featureConfiguration = featureConfiguration;
    }

    /**
     * Fetches this ConfiguredFeature's Holder, ensuring it has been registered.
     * This is the only way you should retrieve the Holder for this ConfiguredFeature!
     */
    public Holder<ConfiguredFeature<?, ?>> holder() {
        this.register(); // Ensure we are registered before returning the holder
        return this.holder;
    }

    /**
     * Registers this ConfiguredFeature if it has not already been registered.
     * Note that this object's id must have been initialized.
     * For internal use only.
     */
    public void register() {
        if (this.registered) return; // Abort if already registered

        ConfiguredFeature<FC, Feature<FC>> configuredFeature = new ConfiguredFeature<>(
                this.feature,
                this.featureConfiguration.get());
        this.holder = BuiltinRegistries.register(
                BuiltinRegistries.CONFIGURED_FEATURE,
                this.id,
                configuredFeature);
        this.setSupplier(() -> configuredFeature);
        this.registered = true;
    }
}
