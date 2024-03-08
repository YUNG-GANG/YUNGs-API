package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PotionBrewing.class)
public interface PotionBrewingAccessor {
    @Invoker
    static void callAddMix(Potion potion, Item item, Potion potion2) {
        throw new UnsupportedOperationException();
    }
}
