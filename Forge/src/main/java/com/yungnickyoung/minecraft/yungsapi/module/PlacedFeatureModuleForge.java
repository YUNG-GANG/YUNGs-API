package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterConfiguredFeature;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPlacedFeature;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

/**
 * Registration of PlacedFeatures.
 */
public class PlacedFeatureModuleForge {
    public static void processEntries() {
        // We subscribe to Register Event because it runs before Common Setup.
        // We subscribe to Item class because it runs after Block registration but before all other Register events.
        // This ensures that any blocks/items used in features will be properly registered,
        // while also guaranteeing our features will be ready to use in time for Biome registration.
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, EventPriority.LOW, PlacedFeatureModuleForge::registerPlacedFeatures);
        attachIds();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PlacedFeatureModuleForge::registerPlacedFeatures);
    }

    private static void attachIds() {
        AutoRegistrationManager.CONFIGURED_FEATURES.forEach(data -> {
            AutoRegisterConfiguredFeature autoRegisterConfiguredFeature = (AutoRegisterConfiguredFeature) data.object();
            // Attach ID if not already attached
            if (autoRegisterConfiguredFeature.id == null) {
                autoRegisterConfiguredFeature.id = data.name();
            }
        });
        AutoRegistrationManager.PLACED_FEATURES.forEach(data -> {
            AutoRegisterPlacedFeature autoRegisterPlacedFeature = (AutoRegisterPlacedFeature) data.object();
            // Attach ID if not already attached
            if (autoRegisterPlacedFeature.id == null) {
                autoRegisterPlacedFeature.id = data.name();
            }
        });
    }

    //    private static void registerPlacedFeatures(final RegistryEvent.Register<Item> event) {
    private static void registerPlacedFeatures(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // First we register all configured features.
            ConfiguredFeatureModuleForge.registerConfiguredFeatures();

            // Next we register all placed features.
            AutoRegistrationManager.PLACED_FEATURES.stream()
                    .filter(data -> !data.processed())
                    .forEach(PlacedFeatureModuleForge::register);
        });
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterPlacedFeature autoRegisterPlacedFeature = (AutoRegisterPlacedFeature) data.object();
        // Attach ID if not already attached
        if (autoRegisterPlacedFeature.id == null) {
            autoRegisterPlacedFeature.id = data.name();
        }

        // Register if not yet registered
        autoRegisterPlacedFeature.register();

//        Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder = autoRegisterPlacedFeature.getConfiguredFeatureHolder();
//        List<PlacementModifier> placementModifiers = autoRegisterPlacedFeature.placementModifiers();
//        PlacedFeature placedFeature = new PlacedFeature(Holder.hackyErase(configuredFeatureHolder), List.copyOf(placementModifiers));
//        Holder<PlacedFeature> placedFeatureHolder = BuiltinRegistries.register(
//                BuiltinRegistries.PLACED_FEATURE,
//                data.name(),
//                placedFeature);
//        autoRegisterPlacedFeature.setSupplier(() -> placedFeature);
//        autoRegisterPlacedFeature.setHolder(placedFeatureHolder);
        data.markProcessed();
    }
}