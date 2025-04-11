package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class NeoForgeBlockEntityTypeHelper implements IBlockEntityTypeHelper {
    @Override
    public <T extends BlockEntity> BlockEntityType<T> build(AutoRegisterBlockEntityType.Builder<T> builder) {
        return new BlockEntityType<>(builder.getFactory()::create, builder.getBlocks());
    }
}