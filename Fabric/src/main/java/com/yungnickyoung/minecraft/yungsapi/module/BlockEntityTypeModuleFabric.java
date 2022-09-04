package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Registration of BlockEntityTypes.
 */
public class BlockEntityTypeModuleFabric {
    public static void init() {
        AutoRegistrationManager.BLOCK_ENTITY_TYPES.forEach(BlockEntityTypeModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterBlockEntityType<? extends BlockEntity> autoRegisterBlockEntityType = (AutoRegisterBlockEntityType) data.object();
        BlockEntityType<?> blockEntityType = autoRegisterBlockEntityType.get();

        // Register block entity type
        Registry.register(Registry.BLOCK_ENTITY_TYPE, data.name(), blockEntityType);
    }
}
