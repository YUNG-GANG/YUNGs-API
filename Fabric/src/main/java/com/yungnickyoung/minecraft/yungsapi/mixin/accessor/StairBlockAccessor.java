package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StairBlock.class)
public interface StairBlockAccessor {
    @Invoker("<init>")
    static StairBlock createStairBlock(BlockState blockState, BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
