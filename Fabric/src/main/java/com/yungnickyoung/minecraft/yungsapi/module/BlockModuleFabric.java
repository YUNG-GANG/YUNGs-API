package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

/**
 * Registration of Blocks and BlockItems.
 */
public class BlockModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.BLOCKS.stream()
                .filter(data -> !data.processed())
                .forEach(BlockModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        Block block = autoRegisterBlock.get();

        // Register block
        Registry.register(Registry.BLOCK, data.name(), block);

        // Register item if applicable
        if (autoRegisterBlock.hasItemProperties()) {
            ItemModuleFabric.registerBlockItem(data.name(), block, autoRegisterBlock.getItemProperties().get());
        }

        data.markProcessed();
    }
}