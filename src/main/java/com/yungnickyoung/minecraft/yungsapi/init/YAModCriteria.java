package com.yungnickyoung.minecraft.yungsapi.init;

import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import com.yungnickyoung.minecraft.yungsapi.criteria.SafeStructurePositionTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of custom criteria triggers for advancements.
 */
public class YAModCriteria {
    public static final SafeStructurePositionTrigger SAFE_STRUCTURE_POSITION_TRIGGER = new SafeStructurePositionTrigger(new ResourceLocation(YungsApi.MOD_ID, "structure_location"));

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(YAModCriteria::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(SafeStructurePositionTrigger::playerTick);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CriteriaTriggers.register(SAFE_STRUCTURE_POSITION_TRIGGER);
        });
    }
}
