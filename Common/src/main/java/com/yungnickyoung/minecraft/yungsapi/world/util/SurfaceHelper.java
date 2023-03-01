package com.yungnickyoung.minecraft.yungsapi.world.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SurfaceHelper {
    private SurfaceHelper() {} // Private constructor prevents instantiation

    /**
     * Returns the y-coordinate of the topmost non-air block at the given column position in the world.
     * Returns 1 if somehow no non-air block is found.
     */
    public static int getSurfaceHeight(ChunkAccess chunk, ColumnPos pos) {
        int maxY = chunk.getMaxBuildHeight() - 1;
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(pos.x(), maxY, pos.z());

        // Edge case: blocks go all the way up to build height
        if (chunk.getBlockState(blockPos) != Blocks.AIR.defaultBlockState())
            return maxY;

        for (int y = maxY; y >= 0; y--) {
            BlockState blockState = chunk.getBlockState(blockPos);
            if (blockState != Blocks.AIR.defaultBlockState())
                return y;
            blockPos.move(Direction.DOWN);
        }

        return 1; // Surface somehow not found
    }
}
