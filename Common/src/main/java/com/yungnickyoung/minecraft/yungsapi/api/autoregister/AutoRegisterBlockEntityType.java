package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.mojang.datafixers.types.Type;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Wrapper for registering {@link BlockEntityType}s with AutoRegister.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 * @AutoRegister("icicle_block_entity")
 * public static AutoRegisterBlockEntityType<IcicleBlockEntity> ICICLE = AutoRegisterBlockEntityType
 *         .of(() -> AutoRegisterBlockEntityType.Builder
 *                 .of(IcicleBlockEntity::new, BlockModule.ICICLE.get())
 *                 .build());
 * }
 * </pre>
 */
public class AutoRegisterBlockEntityType <T extends BlockEntity> extends AutoRegisterEntry<BlockEntityType<T>> {
    public static <U extends BlockEntity> AutoRegisterBlockEntityType<U> of(Supplier<BlockEntityType<U>> blockSupplier) {
        return new AutoRegisterBlockEntityType<>(blockSupplier);
    }

    private AutoRegisterBlockEntityType(Supplier<BlockEntityType<T>> blockSupplier) {
        super(blockSupplier);
    }

    /**
     * Platform-agnostic builder for {@link BlockEntityType}s.
     */
    public static class Builder <T extends BlockEntity> {
        private final BlockEntitySupplier<? extends T> factory;
        private final Block[] blocks;

        private Builder(BlockEntitySupplier<? extends T> factory, Block[] blocks) {
            this.factory = factory;
            this.blocks = blocks;
        }

        /**
         * Creates a new {@link Builder} for the given {@link BlockEntityType} factory and {@link Block}s.
         * @param factory the {@link BlockEntityType} factory
         * @param blocks the {@link Block}s
         * @return the newly instantiated {@link Builder}
         * @param <T> the {@link BlockEntity} type
         */
        public static <T extends BlockEntity> Builder<T> of(BlockEntitySupplier<? extends T> factory, Block... blocks) {
            return new Builder<>(factory, blocks);
        }

        public BlockEntityType<T> build() {
            return build(null);
        }

        public BlockEntityType<T> build(Type<?> type) {
            return Services.BLOCK_ENTITY_TYPE_HELPER.build(this, type);
        }

        @ApiStatus.Internal
        public BlockEntitySupplier<? extends T> getFactory() {
            return factory;
        }

        @ApiStatus.Internal
        public Block[] getBlocks() {
            return blocks;
        }

        @FunctionalInterface
        public interface BlockEntitySupplier<T extends BlockEntity> {
            T create(BlockPos blockPos, BlockState blockState);
        }
    }
}