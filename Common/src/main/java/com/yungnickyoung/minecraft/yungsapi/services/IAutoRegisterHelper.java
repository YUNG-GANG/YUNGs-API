package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterFieldRouter;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

import java.util.List;
import java.util.function.Supplier;

public interface IAutoRegisterHelper {
    void prepareAllAutoRegisterFields();
    void invokeAllAutoRegisterMethods();
//    void registerEntityAttributes(AutoRegisterEntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder);
    void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion);
}
