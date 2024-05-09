package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of BlockEntityTypes.
 */
public class BlockEntityTypeModuleNeoForge {
    private static final Map<String, DeferredRegister<BlockEntityType<?>>> registersByModId = new HashMap<>();

    public static void processEntries() {
        AutoRegistrationManager.BLOCK_ENTITY_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(BlockEntityTypeModuleNeoForge::register);
    }

    private static void register(AutoRegisterField data) {
        // Create & register deferred registry for current mod, if necessary
        String modId = data.name().getNamespace();
        if (!registersByModId.containsKey(modId)) {
            DeferredRegister<BlockEntityType<?>> deferredRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
            deferredRegister.register(YungsApiNeoForge.loadingContextEventBus);
            registersByModId.put(modId, deferredRegister);
        }

        AutoRegisterBlockEntityType autoRegisterBlockEntityType = (AutoRegisterBlockEntityType<?>) data.object();
        Supplier<BlockEntityType<?>> blockEntityTypeSupplier = autoRegisterBlockEntityType.getSupplier();

        // Register block
        DeferredRegister<BlockEntityType<?>> deferredRegister = registersByModId.get(modId);
        DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> registryObject = deferredRegister.register(data.name().getPath(), blockEntityTypeSupplier);

        // Update the supplier to use the RegistryObject so that it will be properly updated later on
        autoRegisterBlockEntityType.setSupplier(registryObject);

        data.markProcessed();
    }
}