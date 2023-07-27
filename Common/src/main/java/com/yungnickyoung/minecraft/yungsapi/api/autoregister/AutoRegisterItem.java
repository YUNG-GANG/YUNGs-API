package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Wrapper for registering {@link Item}s with AutoRegister.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 * @AutoRegister("ice_mob_spawn_egg")
 * public static AutoRegisterItem ICE_MOB_SPAWN_EGG = AutoRegisterItem.of(() ->
 *         new SpawnEggItem(
 *                 EntityTypeModule.ICE_MOB.get(), 10798332, 15002876,
 *                 new Item.Properties().tab(ExampleMod.TAB_GENERAL.get())));
 * }
 * </pre>
 */
public class AutoRegisterItem extends AutoRegisterEntry<Item> {
    public static AutoRegisterItem of(Supplier<Item> itemSupplier) {
        return new AutoRegisterItem(itemSupplier);
    }

    private AutoRegisterItem(Supplier<Item> itemSupplier) {
        super(itemSupplier);
    }
}