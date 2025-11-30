package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Potion.class)
public interface PotionAccessor {
    @Accessor
    String getName();

    @Accessor
    @Mutable
    void setName(String name);
}
