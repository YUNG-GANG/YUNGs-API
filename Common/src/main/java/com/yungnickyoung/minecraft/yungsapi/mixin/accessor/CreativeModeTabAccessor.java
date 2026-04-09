package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {
    @Accessor("DEFAULT_BACKGROUND")
    static Identifier getDefaultBackground() {
        throw new AssertionError();
    }
}