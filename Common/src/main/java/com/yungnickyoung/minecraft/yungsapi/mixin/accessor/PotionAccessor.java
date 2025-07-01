package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Potion.class)
public interface PotionAccessor {
    @Accessor
    String getName();

    @Accessor
    @Mutable
    void setName(String name);
}
