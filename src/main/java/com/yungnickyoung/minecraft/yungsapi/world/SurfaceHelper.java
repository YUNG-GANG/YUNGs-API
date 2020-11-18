package com.yungnickyoung.minecraft.yungsapi.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

public class SurfaceHelper {
    private SurfaceHelper() {} // Private constructor prevents instantiation

    /**
     * Returns the y-coordinate of the topmost non-air block at the given column position in the world.
     * Returns 1 if somehow no non-air block is found.
     */
    public static int getSurfaceHeight(Chunk chunkIn, ColumnPos pos) {
        BlockPos.Mutable blockPos = new BlockPos.Mutable(pos.x, 255, pos.z);

        // Edge case: blocks go all the way up to build height
        BlockPos topPos = new BlockPos(pos.x, 255, pos.z);
        if (chunkIn.getBlockState(topPos) != Blocks.AIR.getDefaultState())
            return 255;

        for (int y = 255; y >= 0; y--) {
            BlockState blockState = chunkIn.getBlockState(blockPos);
            if (blockState != Blocks.AIR.getDefaultState())
                return y;
            blockPos.move(Direction.DOWN);
        }

        return 1; // Surface somehow not found
    }
}
