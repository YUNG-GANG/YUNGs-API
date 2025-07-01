package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterUtils;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PotionAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Potions and brewing recipes.
 */
public class PotionModuleForge {
    public static final List<IBrewingRecipe> BREWING_RECIPES = new ArrayList<>();

    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PotionModuleForge::registerPotions);
        MinecraftForge.EVENT_BUS.addListener(PotionModuleForge::registerBrewingRecipes);
    }

    private static void registerPotions(RegisterEvent event) {
        event.register(Registries.POTION, helper -> {
            AutoRegistrationManager.POTIONS.stream()
                    .filter(data -> !data.processed())
                    .forEach(data -> registerPotion(data, helper));
        });
    }

    private static void registerPotion(AutoRegisterField data, RegisterEvent.RegisterHelper<Potion> helper) {
        AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion) data.object();
        Potion potion = autoRegisterPotion.get();

        // If the potion does not have a name, set it based on the annotation data
        if (((PotionAccessor) potion).getName() == null) {
            String name = data.name().getNamespace() + "." + data.name().getPath();
            ((PotionAccessor) potion).setName(name);
        }

        // We directly reference the registry instead of using the helper so we can set the holder on the AutoRegisterPotion instance.
        // At the time of writing, the helper does not provide a way to get the holder after registration.
        Holder<Potion> holder = Registry.registerForHolder(BuiltInRegistries.POTION, data.name(), potion);
        ((AutoRegisterPotion) data.object()).setHolder(holder);

        data.markProcessed();
    }

    /**
     * Registers all recipes added with {@link AutoRegisterUtils#registerBrewingRecipe}.
     * Note that usage of the aforementioned method should be performed in a method annotated
     * with {@link AutoRegister}. This method is explicitly called after all such methods have been invoked,
     * during CommonSetup.
     */
    private static void registerBrewingRecipes(BrewingRecipeRegisterEvent event) {
        BREWING_RECIPES.forEach(event::addRecipe);
    }

    public record BrewingRecipe(Holder<Potion> input, Supplier<Item> ingredient,
                                Holder<Potion> output) implements IBrewingRecipe {
        @Override
        public boolean isInput(ItemStack itemStack) {
            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            return potionContents.is(this.input);
        }

        @Override
        public boolean isIngredient(ItemStack itemStack) {
            return itemStack.getItem() == this.ingredient.get();
        }

        @Override
        @ParametersAreNonnullByDefault
        public @NotNull ItemStack getOutput(ItemStack inputStack, ItemStack ingredientStack) {
            return isInput(inputStack) && isIngredient(ingredientStack)
                    ? PotionContents.createItemStack(inputStack.getItem(), this.output)
                    : ItemStack.EMPTY;
        }
    }
}
