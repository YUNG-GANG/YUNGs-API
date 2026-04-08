package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTab.Builder.class)
public interface CreativeModeTabBuilderAccessor {
    @Accessor("EMPTY_GENERATOR")
    static CreativeModeTab.DisplayItemsGenerator getEmptyGenerator() {
        throw new AssertionError();
    }
}