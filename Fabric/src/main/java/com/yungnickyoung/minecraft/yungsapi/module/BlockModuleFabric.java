package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StairBlockAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.*;
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

        // Register associated Blocks & their BlockItems, if applicable
        if (autoRegisterBlock.hasStairs()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_stairs");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block stairBlock = StairBlockAccessor.createStairBlock(block.defaultBlockState(), props);
            Registry.register(BuiltInRegistries.BLOCK, name, stairBlock);
            autoRegisterBlock.setStairs(stairBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, stairBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasSlab()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_slab");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block slabBlock = new SlabBlock(props);
            Registry.register(BuiltInRegistries.BLOCK, name, slabBlock);
            autoRegisterBlock.setSlab(slabBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, slabBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasFence()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_fence");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block fenceBlock = new FenceBlock(props);
            Registry.register(BuiltInRegistries.BLOCK, name, fenceBlock);
            autoRegisterBlock.setFence(fenceBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, fenceBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasFenceGate()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_fence_gate");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block fenceGateBlock = new FenceGateBlock(autoRegisterBlock.getFenceGateWoodType(), props);
            Registry.register(BuiltInRegistries.BLOCK, name, fenceGateBlock);
            autoRegisterBlock.setFenceGate(fenceGateBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, fenceGateBlock, autoRegisterBlock.getItemProperties().get());
            }
        }
        if (autoRegisterBlock.hasWall()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_wall");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block wallBlock = new WallBlock(props);
            Registry.register(BuiltInRegistries.BLOCK, name, wallBlock);
            autoRegisterBlock.setWall(wallBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                ItemModuleFabric.registerBlockItem(name, wallBlock, autoRegisterBlock.getItemProperties().get());
            }
        }

        data.markProcessed();
    }
}