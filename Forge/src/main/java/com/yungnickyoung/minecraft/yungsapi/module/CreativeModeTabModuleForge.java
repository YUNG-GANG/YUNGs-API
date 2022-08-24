package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Initialization of creative mode tabs.
 */
public class CreativeModeTabModuleForge {
    /**
     * Map for caching tabs that have already initialized.
     * Prevents potential duplicate initialization and cached object instance replacement.
     */
    private static final Map<String, AutoRegisterCreativeTab> initializedTabs = new HashMap<>();

    public static void init() {
        // We subscribe to Register Event because it runs before Common Setup.
        // We subscribe to Block class because it runs before all other Register events, ensuring creative tabs will be initialized first.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.HIGHEST, CreativeModeTabModuleForge::initializeTabs);
    }

    private static void initializeTabs(final RegisterEvent event) {
        event.register(Registry.BLOCK_REGISTRY, helper -> {
            AutoRegistrationManager.CREATIVE_MODE_TABS.forEach(data -> {
                // Check cache to see if we already initialized this tab
                ResourceLocation resourceLocation = data.name();
                String name = String.format("%s.%s", resourceLocation.getNamespace(), resourceLocation.getPath());
                if (initializedTabs.containsKey(name)) {
                    return;
                }

                // Extract data
                AutoRegisterCreativeTab autoRegisterCreativeTab = (AutoRegisterCreativeTab) data.object();
                Supplier<ItemStack> itemStackSupplier = autoRegisterCreativeTab.getIconItemStackSupplier();

                // Create tab
                CreativeModeTab creativeModeTab = new CreativeModeTab(name) {
                    @Override
                    public ItemStack makeIcon() {
                        return itemStackSupplier.get();
                    }
                };

                // Update supplier to retrieve tab
                autoRegisterCreativeTab.setSupplier(() -> creativeModeTab);

                // Add to cache
                initializedTabs.put(name, autoRegisterCreativeTab);
            });
        });
    }
}