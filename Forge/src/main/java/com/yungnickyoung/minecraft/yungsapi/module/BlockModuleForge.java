package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of Blocks and BlockItems.
 */
public class BlockModuleForge {
    private static final Map<String, DeferredRegister<Block>> registersByModId = new HashMap<>();

    public static void init() {
        AutoRegistrationManager.BLOCKS.forEach(BlockModuleForge::register);
    }

    private static void register(RegisterData data) {
        // Create & register deferred registry for current mod's blocks, if necessary
        String modId = data.name().getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<Block> deferredRegister = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
            registersByModId.put(modId, deferredRegister);
        }

        AutoRegisterBlock autoRegisterBlock = (AutoRegisterBlock) data.object();
        Supplier<Block> blockSupplier = autoRegisterBlock.getSupplier();

        // Register block
        DeferredRegister<Block> deferredRegister = registersByModId.get(modId);
        RegistryObject<Block> registryObject = deferredRegister.register(data.name().getPath(), blockSupplier);

        // Update the supplier to use the RegistryObject so that it will be properly updated later on
        autoRegisterBlock.setSupplier(registryObject);

        // Register item if applicable
        if (autoRegisterBlock.hasItemProperties()) {
            ItemModuleForge.registerBlockItem(data.name(), registryObject, autoRegisterBlock.getItemProperties());
        }
    }
}
