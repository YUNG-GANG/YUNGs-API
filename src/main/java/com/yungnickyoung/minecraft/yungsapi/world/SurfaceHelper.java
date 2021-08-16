package com.yungnickyoung.minecraft.yungsapi.world;

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
    public static int getSurfaceHeight(ChunkAccess chunkIn, ColumnPos pos) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(pos.x, 255, pos.z);

        // Edge case: blocks go all the way up to build height
        BlockPos topPos = new BlockPos(pos.x, 255, pos.z);
        if (chunkIn.getBlockState(topPos) != Blocks.AIR.defaultBlockState())
            return 255;

        for (int y = 255; y >= 0; y--) {
            BlockState blockState = chunkIn.getBlockState(blockPos);
            if (blockState != Blocks.AIR.defaultBlockState())
                return y;
            blockPos.move(Direction.DOWN);
        }

        return 1; // Surface somehow not found
    }
}
