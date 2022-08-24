package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Registration of Blocks and BlockItems.
 */
public class BlockModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BlockModuleForge::registerBlocks);
    }

    private static void registerBlocks(RegisterEvent event) {
        event.register(Registry.BLOCK_REGISTRY, helper -> {
            AutoRegistrationManager.BLOCKS.forEach(data -> {
                AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
                Block block = autoRegisterBlock.get();
                helper.register(data.name(), block);
            });
        });
    }
}