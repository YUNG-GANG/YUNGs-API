package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Registration of BlockEntityTypes.
 */
public class BlockEntityTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.BLOCK_ENTITY_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(BlockEntityTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterBlockEntityType autoRegisterBlockEntityType = (AutoRegisterBlockEntityType) data.object();
        BlockEntityType<?> blockEntityType = (BlockEntityType<?>) autoRegisterBlockEntityType.get();

        // Register block entity type
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, data.name(), blockEntityType);
        data.markProcessed();
    }
}