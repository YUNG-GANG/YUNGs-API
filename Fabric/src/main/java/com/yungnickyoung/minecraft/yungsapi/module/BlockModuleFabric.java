package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

/**
 * Registration of Blocks and BlockItems.
 */
public class BlockModuleFabric {
    public static void init() {
        AutoRegistrationManager.BLOCKS.forEach(BlockModuleFabric::register);
    }

    private static void register(RegisterData data) {
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        Block block = autoRegisterBlock.get();

        // Register block
        Registry.register(Registry.BLOCK, data.name(), block);

        // Register item if applicable
        if (autoRegisterBlock.hasItemProperties()) {
            ItemModuleFabric.registerBlockItem(data.name(), block, autoRegisterBlock.getItemProperties().get());
        }
    }
}