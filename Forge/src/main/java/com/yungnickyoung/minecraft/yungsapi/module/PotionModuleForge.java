package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Potions.
 */
public class PotionModuleForge {
    public static final List<IBrewingRecipe> BREWING_RECIPES = new ArrayList<>();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Potion.class, PotionModuleForge::registerPotions);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PotionModuleForge::commonSetup);
    }

    private static void registerPotions(RegistryEvent.Register<Potion> event) {
        // Register potions
        AutoRegistrationManager.POTIONS.forEach(data -> {
            AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion) data.object();
            MobEffectInstance mobEffectInstance = autoRegisterPotion.getMobEffectInstance();
            String name = data.name().getNamespace() + "." + data.name().getPath();
            Potion potion = new Potion(name, mobEffectInstance);
            autoRegisterPotion.setSupplier(() -> potion);

            // Register
            potion.setRegistryName(data.name());
            event.getRegistry().register(potion);
        });
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        BREWING_RECIPES.forEach(BrewingRecipeRegistry::addRecipe);
    }

    public record BrewingRecipe(Supplier<Potion> input, Supplier<Item> ingredient, Supplier<Potion> output) implements IBrewingRecipe {
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
