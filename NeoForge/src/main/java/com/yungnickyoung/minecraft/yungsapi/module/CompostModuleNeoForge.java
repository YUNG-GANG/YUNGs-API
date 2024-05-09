package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.Map;

/**
 * Handles items to be added as compostable.
 */
public class CompostModuleNeoForge {
    public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap<>();

    /**
     * Registers all recipes added with {@link AutoRegisterUtils#addCompostableItem}.
     * Note that usage of the aforementioned method should be performed in a method annotated
     * with {@link AutoRegister}. This method is explicitly called after all such methods have been invoked,
     * during CommonSetup.
     */
    public static void registerCompostables() {
        // TODO ComposterBlock.COMPOSTABLES will be deprecated,
        //  move to using json (see NeoForgeDataMaps.COMPOSTABLES) or switch to RegisterEvent
        ComposterBlock.COMPOSTABLES.putAll(COMPOSTABLES);
    }
}
