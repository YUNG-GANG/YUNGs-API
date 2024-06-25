package com.yungnickyoung.minecraft.yungsapi.module;


import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterUtils;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Potions and brewing recipes.
 */
public class PotionModuleNeoForge {
    public static final List<IBrewingRecipe> BREWING_RECIPES = new ArrayList<>();

    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildAutoRegistrar(Registries.POTION, AutoRegistrationManager.POTIONS, PotionModuleNeoForge::buildPotion));
        NeoForge.EVENT_BUS.addListener(PotionModuleNeoForge::registerBrewingRecipes);
    }

    private static Potion buildPotion(AutoRegisterField data) {
        AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion) data.object();
        MobEffectInstance mobEffectInstance = autoRegisterPotion.getMobEffectInstance();
        String name = data.name().getNamespace() + "." + data.name().getPath();
        Potion potion = new Potion(name, mobEffectInstance);
        autoRegisterPotion.setSupplier(() -> potion);

        // Return for registering
        return potion;
    }

    /**
     * Registers all recipes added with {@link AutoRegisterUtils#registerBrewingRecipe}.
     * Note that usage of the aforementioned method should be performed in a method annotated
     * with {@link AutoRegister}. This method is explicitly called after all such methods have been invoked,
     * during CommonSetup.
     */
    private static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        BREWING_RECIPES.forEach(recipe -> event.getBuilder().addRecipe(recipe));
    }

    public record BrewingRecipe(Supplier<Potion> input, Supplier<Item> ingredient,
                                Supplier<Potion> output) implements IBrewingRecipe {
        @Override
        public boolean isInput(ItemStack itemStack) {
            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            return potionContents.is(Holder.direct(input.get()));
        }

        @Override
        public boolean isIngredient(ItemStack itemStack) {
            return itemStack.getItem() == this.ingredient.get();
        }

        @Override
        public ItemStack getOutput(ItemStack inputStack, ItemStack ingredientStack) {
            return isInput(inputStack) && isIngredient(ingredientStack)
                    ? PotionContents.createItemStack(inputStack.getItem(), Holder.direct(this.output.get()))
                    : ItemStack.EMPTY;
        }
    }
}
