package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of BlockEntityTypes.
 */
public class BlockEntityTypeModuleForge {
    private static final Map<String, DeferredRegister<BlockEntityType<?>>> registersByModId = new HashMap<>();

    public static void init() {
        AutoRegistrationManager.BLOCK_ENTITY_TYPES.forEach(BlockEntityTypeModuleForge::register);
    }

    private static void register(RegisterData data) {
        // Create & register deferred registry for current mod, if necessary
        String modId = data.name().getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<BlockEntityType<?>> deferredRegister = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, modId);
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
            registersByModId.put(modId, deferredRegister);
        }

        AutoRegisterBlockEntityType autoRegisterBlockEntityType = (AutoRegisterBlockEntityType) data.object();
        Supplier<BlockEntityType<?>> blockEntityTypeSupplier = autoRegisterBlockEntityType.getSupplier();

        // Register block
        DeferredRegister<BlockEntityType<?>> deferredRegister = registersByModId.get(modId);
        RegistryObject<BlockEntityType<?>> registryObject = deferredRegister.register(data.name().getPath(), blockEntityTypeSupplier);

        // Update the supplier to use the RegistryObject so that it will be properly updated later on
        autoRegisterBlockEntityType.setSupplier(registryObject);
    }
}
