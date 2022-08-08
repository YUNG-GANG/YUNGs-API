package com.yungnickyoung.minecraft.yungsapi.services;

import com.mojang.datafixers.types.Type;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class FabricBlockEntityTypeHelper implements IBlockEntityTypeHelper {
    @Override
    public <T extends BlockEntity> BlockEntityType<T> build(AutoRegisterBlockEntityType.Builder<T> builder, @Nullable Type<?> type) {
        return FabricBlockEntityTypeBuilder.<T>create(builder.getFactory()::create, builder.getBlocks()).build(type);
    }
}
