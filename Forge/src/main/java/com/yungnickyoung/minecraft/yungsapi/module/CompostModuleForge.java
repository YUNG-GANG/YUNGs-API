package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

/**
 * Handles items to be added as compostable.
 */
public class CompostModuleForge {
    public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap<>();

    /**
     * Registers all recipes added with {@link AutoRegisterUtils#addCompostableItem}.
     *
     * Note that usage of the aforementioned method should be performed in a method annotated
     * with {@link AutoRegister}. This method is explicitly called after all such methods have been invoked,
     * during CommonSetup.
     */
    public static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.putAll(COMPOSTABLES);
    }
}
