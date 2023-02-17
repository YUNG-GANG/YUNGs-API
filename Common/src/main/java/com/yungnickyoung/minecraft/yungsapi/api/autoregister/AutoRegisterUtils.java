package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public class AutoRegisterUtils {
    public static void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion) {
        Services.AUTO_REGISTER.registerBrewingRecipe(inputPotion, ingredient, outputPotion);
    }

    public static void addCompostableItem(Supplier<Item> ingredient, float compostChance) {
        Services.AUTO_REGISTER.addCompostableItem(ingredient, compostChance);
    }
}
