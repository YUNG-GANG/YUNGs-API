package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Blocks.
 */
public class BlockModuleForge {
    public static final List<ExtraBlockData> EXTRA_BLOCKS = new ArrayList<>();

    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, BlockModuleForge::registerBlocks);
    }

    private static void registerBlocks(RegistryEvent.Register<Block> event) {
        AutoRegistrationManager.BLOCKS.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerBlock(data, event.getRegistry()));
    }

    private static void registerBlock(AutoRegisterField data, IForgeRegistry<Block> registry) {
        // Register block
        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        Block block = autoRegisterBlock.get();
        block.setRegistryName(data.name());
        registry.register(block);

        String namespace = data.name().getNamespace();
        String path = data.name().getPath();

        // Register associated blocks, if applicable
        if (autoRegisterBlock.hasStairs()) {
            Block stairBlock = new StairBlock(block::defaultBlockState, BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_stairs");
            stairBlock.setRegistryName(name);
            registry.register(stairBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(stairBlock, autoRegisterBlock.getItemProperties()));
            }
        }
        if (autoRegisterBlock.hasSlab()) {
            Block slabBlock = new SlabBlock(BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_slab");
            slabBlock.setRegistryName(name);
            registry.register(slabBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(slabBlock, autoRegisterBlock.getItemProperties()));
            }
        }
        if (autoRegisterBlock.hasFence()) {
            Block fenceBlock = new FenceBlock(BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_fence");
            fenceBlock.setRegistryName(name);
            registry.register(fenceBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(fenceBlock, autoRegisterBlock.getItemProperties()));
            }
        }
        if (autoRegisterBlock.hasFenceGate()) {
            Block fenceGateBlock = new FenceGateBlock(BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_fence_gate");
            fenceGateBlock.setRegistryName(name);
            registry.register(fenceGateBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(fenceGateBlock, autoRegisterBlock.getItemProperties()));
            }
        }
        if (autoRegisterBlock.hasWall()) {
            Block wallBlock = new WallBlock(BlockBehaviour.Properties.copy(block));
            ResourceLocation name = new ResourceLocation(namespace, path + "_wall");
            wallBlock.setRegistryName(name);
            registry.register(wallBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(wallBlock, autoRegisterBlock.getItemProperties()));
            }
        }

        //        data.markProcessed(); Don't mark as processed, as this will be done by the Items module when checking for BlockItems
    }

    public record ExtraBlockData(Block block, Supplier<Item.Properties> itemProperties) {
    }
}
