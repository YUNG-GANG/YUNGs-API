package com.yungnickyoung.minecraft.yungsapi;

import com.yungnickyoung.minecraft.yungsapi.world.structure.locate.LocateReplacerDataPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(YungsApiCommon.MOD_ID)
public class YungsApiForge {
    public YungsApiForge(FMLJavaModLoadingContext context) {
        YungsApiCommon.init();
        context.getModEventBus().addListener(YungsApiForge::addPackSource);
    }

    private static void addPackSource(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(new LocateReplacerDataPackResources.Source());
        }
    }
}
