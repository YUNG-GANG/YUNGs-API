package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StairBlockAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
        Registry.register(BuiltInRegistries.BLOCK, data.name(), block);

        // Register item if applicable
        if (autoRegisterBlock.hasItemProperties()) {
            ItemModuleFabric.registerBlockItem(data.name(), block, autoRegisterBlock.getItemProperties().get());
        }

        String namespace = data.name().getNamespace();
        String path = data.name().getPath();

        // Register associated blocks & their items, if applicable
        if (autoRegisterBlock.hasStairs()) {
            Block stairBlock = StairBlockAccessor.createStairBlock(block.defaultBlockState(), BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_stairs");
            Registry.register(BuiltInRegistries.BLOCK, name, stairBlock);
            autoRegisterBlock.setStairs(stairBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, stairBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasSlab()) {
            Block slabBlock = new SlabBlock(BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_slab");
            Registry.register(BuiltInRegistries.BLOCK, name, slabBlock);
            autoRegisterBlock.setSlab(slabBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, slabBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasFence()) {
            Block fenceBlock = new FenceBlock(BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_fence");
            Registry.register(BuiltInRegistries.BLOCK, name, fenceBlock);
            autoRegisterBlock.setFence(fenceBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, fenceBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasFenceGate()) {
            Block fenceGateBlock = new FenceGateBlock(BlockBehaviour.Properties.copy(block), autoRegisterBlock.getFenceGateWoodType());
            ResourceLocation name = new ResourceLocation(namespace, path + "_fence_gate");
            Registry.register(BuiltInRegistries.BLOCK, name, fenceGateBlock);
            autoRegisterBlock.setFenceGate(fenceGateBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, fenceGateBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasWall()) {
            Block wallBlock = new WallBlock(BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_wall");
            Registry.register(BuiltInRegistries.BLOCK, name, wallBlock);
            autoRegisterBlock.setWall(wallBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, wallBlock, autoRegisterBlock.getItemProperties().get());
            }
        }

        data.markProcessed();
    }
}