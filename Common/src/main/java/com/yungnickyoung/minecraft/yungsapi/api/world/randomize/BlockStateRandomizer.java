package com.yungnickyoung.minecraft.yungsapi.api.world.randomize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.condition.StructureCondition;
import com.yungnickyoung.minecraft.yungsapi.world.structure.condition.StructureConditionType;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * Describes a set of BlockStates and the probability of each BlockState in the set being chosen.
 * Useful for easily adding random variation to worldgen, especially structures and features.
 */
public class BlockStateRandomizer {
    public static final Codec<BlockStateRandomizer> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    Entry.CODEC.listOf().fieldOf("entries").forGetter((randomizer) -> randomizer.entries),
                    BlockState.CODEC.fieldOf("defaultBlockState").forGetter((randomizer) -> randomizer.defaultBlockState))
            .apply(instance, BlockStateRandomizer::new));

    /**
     * Map of BlockState to its corresponding probability.
     * The total sum of all the probabilities should not exceed 1.
     */
    private List<Entry> entries = new ArrayList<>();

    /**
     * The default BlockState is used for any leftover probability ranges.
     * For example, if the total sum of all the probabilities of the entries is 0.6, then
     * there is a 0.4 chance of the defaultBlockState being selected.
     */
    private BlockState defaultBlockState = Blocks.AIR.defaultBlockState();

    /**
     * Saves this BlockStateRandomizer to a new CompoundTag.
     * @return The CompoundTag
     */
    public CompoundTag saveTag() {
        CompoundTag compoundTag = new CompoundTag();

        // Save default blockstate
        compoundTag.putInt("defaultBlockStateId", Block.BLOCK_STATE_REGISTRY.getId(this.defaultBlockState));

        // Save entries
        ListTag entriesTag = Util.make(new ListTag(), (tag) -> {
            this.entries.forEach((entry) -> {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putInt("entryBlockStateId", Block.BLOCK_STATE_REGISTRY.getId(entry.blockState));
                entryTag.putFloat("entryChance", entry.probability);
                tag.add(entryTag);
            });
        });
        compoundTag.put("entries", entriesTag);

        return compoundTag;
    }

    /**
     * Constructs a new BlockStateRandomizer from a CompoundTag.
     * @param compoundTag The CompoundTag
     */
    public BlockStateRandomizer(CompoundTag compoundTag) {
        this.defaultBlockState = Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("defaultBlockStateId"));
        this.entries = new ArrayList<>();

        ListTag entriesTag = compoundTag.getList("entries", 10);
        entriesTag.forEach(entryTag -> {
            CompoundTag entryCompoundTag = ((CompoundTag) entryTag);
            BlockState blockState = Block.BLOCK_STATE_REGISTRY.byId(entryCompoundTag.getInt("entryBlockStateId"));
            float chance = entryCompoundTag.getFloat("entryChance");
            this.addBlock(blockState, chance);
        });
    }

    /**
     * Constructs a new BlockStateRandomizer from a Map of BlockStates to their corresponding probabilities.
     * @param entries Map of BlockStates to their corresponding probabilities
     * @param defaultBlockState The default BlockState
     */
    public BlockStateRandomizer(Map<BlockState, Float> entries, BlockState defaultBlockState) {
        this.entries = new ArrayList<>();
        entries.forEach(this::addBlock);
        this.defaultBlockState = defaultBlockState;
    }

    /**
     * Constructs a new BlockStateRandomizer from a List of BlockStateRandomizer Entries.
     * @param entries List of BlockStateRandomizer Entries
     * @param defaultBlockState The default BlockState
     */
    public BlockStateRandomizer(List<Entry> entries, BlockState defaultBlockState) {
        this.entries = entries;
        this.defaultBlockState = defaultBlockState;
    }

    /**
     * Constructs a new BlockStateRandomizer with only a default BlockState.
     * @param defaultBlockState The default BlockState
     */
    public BlockStateRandomizer(BlockState defaultBlockState) {
        this.defaultBlockState = defaultBlockState;
    }

    /**
     * Constructs a new BlockStateRandomizer with no default BlockState nor entries.
     */
    public BlockStateRandomizer() {
    }

    /**
     * Convenience factory function that constructs a BlockStateRandomizer from a list of BlockStates.
     * Each BlockState will have equal probability of being chosen.
     */
    public static BlockStateRandomizer from(BlockState... blockStates) {
        BlockStateRandomizer randomizer = new BlockStateRandomizer();
        float chance = 1f / blockStates.length;

        for (BlockState state : blockStates) {
            randomizer.addBlock(state, chance);
        }

        return randomizer;
    }

    /**
     * Adds a BlockState with given chance of being selected.
     * @return The modified BlockStateRandomizer
     */
    public BlockStateRandomizer addBlock(BlockState blockState, float chance) {
        // Abort if BlockState already a part of this randomizer
        if (entries.stream().anyMatch(entry -> entry.blockState.equals(blockState))) {
            YungsApiCommon.LOGGER.warn("WARNING: duplicate block {} added to BlockStateRandomizer!", blockState.toString());
            return this;
        }

        // Attempt to add BlockState to entries
        float currTotal = entries.stream().map(entry -> entry.probability).reduce(Float::sum).orElse(0f);
        float newTotal = currTotal + chance;
        if (newTotal > 1.0F) { // Total probability cannot exceed 1
            YungsApiCommon.LOGGER.warn("WARNING: block {} added to BlockStateRandomizer exceeds max probabiltiy of 1!", blockState.toString());
            return this;
        }
        entries.add(new Entry(blockState, chance));
        return this;
    }

    /**
     * Randomly selects a BlockState from this BlockStateRandomizer.
     * The Random provided should be one used in generation of your structure or feature,
     * to ensure reproducibility for the same world seed.
     */
    public BlockState get(Random random) {
        float target = random.nextFloat();
        float currBottom = 0;

        for (Entry entry : entries) {
            if (currBottom <= target && target < currBottom + entry.probability) {
                return entry.blockState;
            }

            currBottom += entry.probability;
        }

        // No match found
        return this.defaultBlockState;
    }

    /**
     * Randomly selects a BlockState from this BlockStateRandomizer.
     * The RandomSource provided should be one used in generation of your structure or feature,
     * to ensure reproducibility for the same world seed.
     */
    public BlockState get(RandomSource randomSource) {
        float target = randomSource.nextFloat();
        float currBottom = 0;

        for (Entry entry : entries) {
            if (currBottom <= target && target < currBottom + entry.probability) {
                return entry.blockState;
            }

            currBottom += entry.probability;
        }

        // No match found
        return this.defaultBlockState;
    }

    /**
     * Randomly selects a BlockState from this BlockStateRandomizer.
     * The RandomSource provided should be one used in generation of your structure or feature,
     * to ensure reproducibility for the same world seed.
     * Enforces conditions via the StructureContext passed in.
     */
    public BlockState get(RandomSource randomSource, StructureContext ctx) {
        float target = randomSource.nextFloat();
        float currBottom = 0;

        for (Entry entry : entries) {
            if (currBottom <= target && target < currBottom + entry.probability && entry.passesCondition(ctx)) {
                return entry.blockState;
            }

            currBottom += entry.probability;
        }

        // No match found
        return this.defaultBlockState;
    }

    /**
     * Sets the default BlockState.
     * This default BlockState is used for any leftover probability ranges.
     */
    public void setDefaultBlockState(BlockState blockState) {
        this.defaultBlockState = blockState;
    }

    /**
     * Returns a Map of BlockStates to their corresponding probabilities.
     * Does not include the defaultBlockState.
     * @return The Map
     */
    public Map<BlockState, Float> getEntriesAsMap() {
        Map<BlockState, Float> map = new HashMap<>();
        this.entries.forEach(entry -> map.put(entry.blockState, entry.probability));
        return map;
    }

    /**
     * Returns a List of BlockStateRandomizer Entries.
     * @return The List
     */
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * Returns the default BlockState.
     * @return The default BlockState
     */
    public BlockState getDefaultBlockState() {
        return defaultBlockState;
    }

    /**
     * Represents a BlockState and its corresponding probability of being chosen.
     */
    public static class Entry {
        public static Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        BlockState.CODEC.fieldOf("blockState").forGetter(entry -> entry.blockState),
                        Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(entry -> entry.probability),
                        StructureConditionType.CONDITION_CODEC.optionalFieldOf("condition").forGetter(entry -> entry.condition))
                .apply(instance, Entry::new));

        public BlockState blockState;
        public float probability;
        public Optional<StructureCondition> condition; // Conditions are ONLY supported via JSON

        public Entry(BlockState blockState, float probability) {
            this.blockState = blockState;
            this.probability = probability;
        }

        public Entry(BlockState blockState, float probability, Optional<StructureCondition> condition) {
            this.blockState = blockState;
            this.probability = probability;
            this.condition = condition;
        }

        public boolean passesCondition(StructureContext ctx) {
            return this.condition.isEmpty() || this.condition.get().passes(ctx);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Entry) {
                return this.blockState.equals(((Entry) obj).blockState);
            } else if (obj instanceof BlockState) {
                return this.blockState.equals(obj);
            }
            return false;
        }
    }
}