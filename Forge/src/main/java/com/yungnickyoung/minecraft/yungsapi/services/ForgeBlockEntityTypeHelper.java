package com.yungnickyoung.minecraft.yungsapi.services;

import com.mojang.datafixers.types.Type;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class ForgeBlockEntityTypeHelper implements IBlockEntityTypeHelper {
    @Override
    public <T extends BlockEntity> BlockEntityType<T> build(AutoRegisterBlockEntityType.Builder<T> builder, @Nullable Type<?> type) {
        return BlockEntityType.Builder.<T>of(builder.getFactory()::create, builder.getBlocks()).build(type);
    }
}
