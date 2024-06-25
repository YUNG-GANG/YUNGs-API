package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Blocks.
 */
public class BlockModuleForge {
    public static final List<ExtraBlockData> EXTRA_BLOCKS = new ArrayList<>();

    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BlockModuleForge::registerBlocks);
    }

    private static void registerBlocks(RegisterEvent event) {
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
            Block stairBlock = new StairBlock(block.defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(block));
            ResourceLocation name = ResourceLocation.fromNamespaceAndPath(namespace, path + "_stairs");
            helper.register(name, stairBlock);
            autoRegisterBlock.setStairs(stairBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(stairBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasSlab()) {
            Block slabBlock = new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(block));
            ResourceLocation name = ResourceLocation.fromNamespaceAndPath(namespace, path + "_slab");
            helper.register(name, slabBlock);
            autoRegisterBlock.setSlab(slabBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(slabBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasFence()) {
            Block fenceBlock = new FenceBlock(BlockBehaviour.Properties.ofLegacyCopy(block));
            ResourceLocation name = ResourceLocation.fromNamespaceAndPath(namespace, path + "_fence");
            helper.register(name, fenceBlock);
            autoRegisterBlock.setFence(fenceBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(fenceBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasFenceGate()) {
            Block fenceGateBlock = new FenceGateBlock(autoRegisterBlock.getFenceGateWoodType(), BlockBehaviour.Properties.ofLegacyCopy(block));
            ResourceLocation name = ResourceLocation.fromNamespaceAndPath(namespace, path + "_fence_gate");
            helper.register(name, fenceGateBlock);
            autoRegisterBlock.setFenceGate(fenceGateBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(fenceGateBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }
        if (autoRegisterBlock.hasWall()) {
            Block wallBlock = new WallBlock(BlockBehaviour.Properties.ofLegacyCopy(block));
            ResourceLocation name = ResourceLocation.fromNamespaceAndPath(namespace, path + "_wall");
            helper.register(name, wallBlock);
            autoRegisterBlock.setWall(wallBlock);
            if (autoRegisterBlock.hasItemProperties()) {
                EXTRA_BLOCKS.add(new ExtraBlockData(wallBlock, autoRegisterBlock.getItemProperties(), name));
            }
        }

        data.markProcessed();
    }

    public record ExtraBlockData(Block block, Supplier<Item.Properties> itemProperties, ResourceLocation blockRegisteredName) {
    }
}