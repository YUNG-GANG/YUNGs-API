package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Blocks.
 */
public class BlockModuleNeoForge {
    public static final List<ExtraBlockData> EXTRA_BLOCKS = new ArrayList<>();

    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(BlockModuleNeoForge::registerBlocks);
    }

    private static void registerBlocks(final RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> AutoRegistrationManager.BLOCKS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerBlock(data, helper)));
    }

    private static void registerBlock(AutoRegisterField data, RegisterEvent.RegisterHelper<Block> helper) {
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        Block block = autoRegisterBlock.get();
        helper.register(data.name(), block);

        String namespace = data.name().getNamespace();
        String path = data.name().getPath();

        // Register associated Blocks and their BlockItems, if applicable
        if (autoRegisterBlock.hasStairs()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_stairs");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block stairBlock = new StairBlock(block.defaultBlockState(), props);
            helper.register(name, stairBlock);
            autoRegisterBlock.setStairs(stairBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(stairBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasSlab()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_slab");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block slabBlock = new SlabBlock(props);
            helper.register(name, slabBlock);
            autoRegisterBlock.setSlab(slabBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(slabBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasFence()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_fence");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block fenceBlock = new FenceBlock(props);
            helper.register(name, fenceBlock);
            autoRegisterBlock.setFence(fenceBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(fenceBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasFenceGate()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_fence_gate");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block fenceGateBlock = new FenceGateBlock(autoRegisterBlock.getFenceGateWoodType(), props);
            helper.register(name, fenceGateBlock);
            autoRegisterBlock.setFenceGate(fenceGateBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(fenceGateBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasWall()) {
            Identifier name = Identifier.fromNamespaceAndPath(namespace, path + "_wall");
            BlockBehaviour.Properties props = BlockBehaviour.Properties.ofLegacyCopy(block);
            props.setId(ResourceKey.create(Registries.BLOCK, name));
            Block wallBlock = new WallBlock(props);
            helper.register(name, wallBlock);
            autoRegisterBlock.setWall(wallBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(wallBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }

        data.markProcessed();
    }

    public record ExtraBlockData(Block block, Supplier<Item.Properties> itemProperties, Identifier blockRegisteredName) {
    }
}