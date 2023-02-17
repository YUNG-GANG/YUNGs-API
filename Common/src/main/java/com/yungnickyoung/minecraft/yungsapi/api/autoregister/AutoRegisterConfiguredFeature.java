package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.function.Supplier;

public class AutoRegisterConfiguredFeature extends AutoRegisterEntry<ConfiguredFeature<?, ?>> {
    private Holder<ConfiguredFeature<?, ?>> holder;
    private boolean registered = false;
    public ResourceLocation id = null;

    public static AutoRegisterConfiguredFeature of(Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier) {
        return new AutoRegisterConfiguredFeature(configuredFeatureSupplier);
    }

    private AutoRegisterConfiguredFeature(Supplier<ConfiguredFeature<?, ?>> configuredFeatureSupplier) {
        super(configuredFeatureSupplier);
    }

    public Holder<ConfiguredFeature<?, ?>> holder() {
        this.register(); // Ensure we are registered before returning the holder
        return this.holder;
    }

    public void setHolder(Holder<ConfiguredFeature<?, ?>> holder) {
        this.holder = holder;
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void register() {
        if (this.registered) return; // Abort if already registered

        ConfiguredFeature<?, ?> configuredFeature = this.get();
        Holder<ConfiguredFeature<?, ?>> holder = BuiltinRegistries.register(
                BuiltinRegistries.CONFIGURED_FEATURE,
                this.id,
                configuredFeature);
        this.setHolder(holder);
        this.setSupplier(() -> configuredFeature);
        this.registered = true;
    }
}
