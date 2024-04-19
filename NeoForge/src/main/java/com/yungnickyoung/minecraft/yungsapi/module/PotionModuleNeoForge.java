package com.yungnickyoung.minecraft.yungsapi.module;


import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterUtils;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Potions and brewing recipes.
 */
public class PotionModuleNeoForge {
    public static final List<IBrewingRecipe> BREWING_RECIPES = new ArrayList<>();

    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(PotionModuleNeoForge::registerPotions);
    }

    private static void registerPotions(RegisterEvent event) {
        event.register(Registries.POTION, helper -> {
            // Register potions
            AutoRegistrationManager.POTIONS.stream()
                    .filter(data -> !data.processed())
                    .forEach(data -> registerPotion(data, helper));
        });
    }

    private static void registerPotion(AutoRegisterField data, RegisterEvent.RegisterHelper<Potion> helper) {
        AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion) data.object();
        MobEffectInstance mobEffectInstance = autoRegisterPotion.getMobEffectInstance();
        String name = data.name().getNamespace() + "." + data.name().getPath();
        Potion potion = new Potion(name, mobEffectInstance);
        autoRegisterPotion.setSupplier(() -> potion);

        // Register
        helper.register(data.name(), potion);
        data.markProcessed();
    }

    /**
     * Registers all recipes added with {@link AutoRegisterUtils#registerBrewingRecipe}.
     * Note that usage of the aforementioned method should be performed in a method annotated
     * with {@link AutoRegister}. This method is explicitly called after all such methods have been invoked,
     * during CommonSetup.
     */
    public static void registerBrewingRecipes() {
        BREWING_RECIPES.forEach(BrewingRecipeRegistry::addRecipe);
    }

    public record BrewingRecipe(Supplier<Potion> input, Supplier<Item> ingredient,
                                Supplier<Potion> output) implements IBrewingRecipe {
        @Override
        public boolean isInput(ItemStack input) {
            return PotionUtils.getPotion(input) == this.input.get();
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return ingredient.getItem() == this.ingredient.get();
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (!this.isInput(input) || !this.isIngredient(ingredient)) {
                return ItemStack.EMPTY;
            }

            ItemStack itemStack = new ItemStack(input.getItem());
            itemStack.setTag(new CompoundTag());
            PotionUtils.setPotion(itemStack, this.output.get());
            return itemStack;
        }
    }
}
