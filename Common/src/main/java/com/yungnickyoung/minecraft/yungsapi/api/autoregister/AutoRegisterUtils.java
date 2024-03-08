package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public class AutoRegisterUtils {
    /**
     * Registers a brewing recipe with the given input potion, ingredient, and output potion.
     * This function is best used from within a static method with no parameters that is annotated with @AutoRegister.
     * For more information, see the {@link AutoRegister} documentation.
     */
    public static void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion) {
        Services.AUTO_REGISTER.registerBrewingRecipe(inputPotion, ingredient, outputPotion);
    }

    /**
     * Registers an item as compostable with the given compost chance.
     * This function is best used from within a static method with no parameters that is annotated with @AutoRegister.
     * For more information, see the {@link AutoRegister} documentation.
     */
    public static void addCompostableItem(Supplier<Item> ingredient, float compostChance) {
        Services.AUTO_REGISTER.addCompostableItem(ingredient, compostChance);
    }
}
