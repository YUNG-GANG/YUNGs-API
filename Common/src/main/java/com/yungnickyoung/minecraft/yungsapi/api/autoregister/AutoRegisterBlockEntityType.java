package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.mojang.datafixers.types.Type;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class AutoRegisterBlockEntityType <T extends BlockEntity> extends AutoRegisterEntry<BlockEntityType<T>> {
    public static <U extends BlockEntity> AutoRegisterBlockEntityType<U> of(Supplier<BlockEntityType<U>> blockSupplier) {
        return new AutoRegisterBlockEntityType<>(blockSupplier);
    }

    private AutoRegisterBlockEntityType(Supplier<BlockEntityType<T>> blockSupplier) {
        super(blockSupplier);
    }

    public static class Builder <T extends BlockEntity> {
        private final BlockEntitySupplier<? extends T> factory;
        private final Block[] blocks;

        private Builder(BlockEntitySupplier<? extends T> factory, Block[] blocks) {
            this.factory = factory;
            this.blocks = blocks;
        }

        public static <T extends BlockEntity> Builder<T> of(BlockEntitySupplier<? extends T> factory, Block... blocks) {
            return new Builder<>(factory, blocks);
        }

        public BlockEntityType<T> build() {
            return build(null);
        }

        public BlockEntityType<T> build(Type<?> type) {
            return Services.BLOCK_ENTITY_TYPE_HELPER.build(this, type);
        }

        public BlockEntitySupplier<? extends T> getFactory() {
            return factory;
        }

        public Block[] getBlocks() {
            return blocks;
        }

        @FunctionalInterface
        public interface BlockEntitySupplier<T extends BlockEntity> {
            T create(BlockPos blockPos, BlockState blockState);
        }
    }
}