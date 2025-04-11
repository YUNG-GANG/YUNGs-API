package com.yungnickyoung.minecraft.yungsapi.services;

import com.mojang.datafixers.types.Type;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;

public class ForgeBlockEntityTypeHelper implements IBlockEntityTypeHelper {
    @Override
    public <T extends BlockEntity> BlockEntityType<T> build(AutoRegisterBlockEntityType.Builder<T> builder) {
        return new BlockEntityType<>(builder.getFactory()::create, new HashSet<>(Arrays.asList(builder.getBlocks())));
    }
}