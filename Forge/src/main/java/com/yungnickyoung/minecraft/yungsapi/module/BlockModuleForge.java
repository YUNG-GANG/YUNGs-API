package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Registration of Blocks and BlockItems.
 */
public class BlockModuleForge {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, BlockModuleForge::registerBlocks);
    }

    private static void registerBlocks(RegistryEvent.Register<Block> event) {
        AutoRegistrationManager.BLOCKS.forEach(data -> {
            AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
            Block block = autoRegisterBlock.get();
            block.setRegistryName(data.name());
            event.getRegistry().register(block);
        });
    }
}
