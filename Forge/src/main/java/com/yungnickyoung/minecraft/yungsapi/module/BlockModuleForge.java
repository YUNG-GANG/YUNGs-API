package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of Blocks.
 */
public class BlockModuleForge {
    private static final Map<String, DeferredRegister<Block>> registersByModId = new HashMap<>();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, BlockModuleForge::register);
//        AutoRegistrationManager.BLOCKS.forEach(BlockModuleForge::register);
    }

    private static void register(RegistryEvent.Register<Block> event) {
        AutoRegistrationManager.BLOCKS.forEach(data -> {
            AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
            Block block = autoRegisterBlock.get();
            block.setRegistryName(data.name());
            event.getRegistry().register(block);
        });
    }

//    private static void register(AutoRegisterField data) {
//        // Create & register deferred registry for current mod, if necessary
//        String modId = data.name().getNamespace();
//        if (!registersByModId.containsKey(modId)) {
//            DeferredRegister<Block> deferredRegister = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
//            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
//            registersByModId.put(modId, deferredRegister);
//        }
//
//        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
//        Supplier<Block> blockSupplier = autoRegisterBlock.getSupplier();
//
//        // Register
//        DeferredRegister<Block> deferredRegister = registersByModId.get(modId);
//        RegistryObject<Block> registryObject = deferredRegister.register(data.name().getPath(), blockSupplier);
//
//        // Update the supplier to use the RegistryObject so that it will be properly updated later on
//        autoRegisterBlock.setSupplier(registryObject);
//    }
}
