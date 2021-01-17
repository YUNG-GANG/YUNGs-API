package com.yungnickyoung.minecraft.yungsapi.world;

import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Describes a set of BlockStates and the probability of each BlockState in the set being chosen.
 * This is very useful for easily adding random variation to your structures and features.
 */
public class BlockSetSelector {
    /**
     * Map of BlockState to its corresponding probability.
     * The total sum of all the probabilities should not exceed 1.
     */
    private Map<BlockState, Float> entries = new HashMap<>();

    /**
     * The default BlockState is used for any leftover probability ranges.
     * For example, if the total sum of all the probabilities of the entries is 0.6, then
     * there is a 0.4 chance of the defaultBlockState being selected.
     */
    private BlockState defaultBlockState = Blocks.CAVE_AIR.getDefaultState();

    public BlockSetSelector(BlockState defaultBlockState) {
        this.defaultBlockState = defaultBlockState;
    }

    public BlockSetSelector() {
    }

    /**
     * Convenience function to construct a BlockSetSelector from a list of BlockStates.
     * Each BlockState will have equal probability of being chosen.
     */
    public static BlockSetSelector from(BlockState... blockStates) {
        BlockSetSelector selector = new BlockSetSelector();
        float chance = 1f / blockStates.length;

        for (BlockState state : blockStates) {
            selector.addBlock(state, chance);
        }

        return selector;
    }

    /**
     * Add a BlockState with given chance of being selected.
     * @return The modified BlockSetSelector
     */
    public BlockSetSelector addBlock(BlockState blockState, float chance) {
        // Abort if BlockState already a part of this selector
        if (entries.containsKey(blockState)) {
            YungsApi.LOGGER.warn(String.format("WARNING: duplicate block %s added to BlockSelector!", blockState.toString()));
            return this;
        }

        // Attempt to add BlockState to entries
        float currTotal = entries.values().stream().reduce(Float::sum).orElse(0f);
        float newTotal = currTotal + chance;
        if (newTotal > 1) { // Total probability cannot exceed 1
            YungsApi.LOGGER.warn(String.format("WARNING: block %s added to BlockSelector exceeds max probabiltiy of 1!", blockState.toString()));
            return this;
        }
        entries.put(blockState, chance);
        return this;
    }

    /**
     * Randomly select a BlockState from this BlockSetSelector.
     * The random provided should be one used in generation of your structure or feature,
     * to ensure reproducibility for the same world seed.
     */
    public BlockState get(Random random) {
        float target = random.nextFloat();
        float currBottom = 0;

        for (Map.Entry<BlockState, Float> entry : entries.entrySet()) {
            float chance = entry.getValue();
            if (currBottom <= target && target < currBottom + chance) {
                return entry.getKey();
            }

            currBottom += chance;
        }

        // No match found
        return this.defaultBlockState;
    }

    /**
     * Sets the default BlockState for this selector.
     * The default BlockState is used for any leftover probability ranges.
     */
    public void setDefaultBlockState(BlockState blockState) {
        this.defaultBlockState = blockState;
    }

    public Map<BlockState, Float> getEntries() {
        return entries;
    }

    public BlockState getDefaultBlockState() {
        return defaultBlockState;
    }
}
