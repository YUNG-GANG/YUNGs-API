package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registration of Blocks.
 */
public class BlockModuleForge {
    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, BlockModuleForge::registerBlocks);
    }

    private static void registerBlocks(RegistryEvent.Register<Block> event) {
        AutoRegistrationManager.BLOCKS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerBlock(data, event.getRegistry()));
    }

    private static void registerBlock(AutoRegisterField data, IForgeRegistry<Block> registry) {
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        Block block = autoRegisterBlock.get();
        block.setRegistryName(data.name());
        registry.register(block);
        data.markProcessed();
    }
}
