package com.yungnickyoung.minecraft.yungsapi.api.world.randomize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Describes a set of Items and the probability of each Item in the set being chosen.
 * This is very useful for easily adding random variation to things like armor stands during world generation.
 */
public class ItemRandomizer {
    public static final Codec<ItemRandomizer> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    Entry.CODEC.listOf().fieldOf("entries").forGetter((randomizer) -> randomizer.entries),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("defaultItem").forGetter((randomizer) -> randomizer.defaultItem))
            .apply(instance, ItemRandomizer::new));

    /**
     * Map of Items to their corresponding probabilities.
     * The total sum of all the probabilities should not exceed 1.
     */
    private List<Entry> entries = new ArrayList<>();

    /**
     * The default Item is used for any leftover probability ranges.
     * For example, if the total sum of all the probabilities of the entries is 0.6, then
     * there is a 0.4 chance of the defaultItem being selected.
     */
    private Item defaultItem = Items.AIR;

    /**
     * Saves this ItemRandomizer to a new CompoundTag.
     * @return The CompoundTag
     */
    public CompoundTag saveTag() {
        CompoundTag compoundTag = new CompoundTag();

        // Save default blockstate
        compoundTag.putInt("defaultItemId", BuiltInRegistries.ITEM.getId(this.defaultItem));

        // Save entries
        ListTag entriesTag = Util.make(new ListTag(), (tag) -> {
            this.entries.forEach((entry) -> {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putInt("entryItemId", BuiltInRegistries.ITEM.getId(entry.item));
                entryTag.putFloat("entryChance", entry.probability);
                tag.add(entryTag);
            });
        });
        compoundTag.put("entries", entriesTag);

        return compoundTag;
    }

    /**
     * Constructs a new ItemRandomizer from a CompoundTag.
     * @param compoundTag The CompoundTag
     */
    public ItemRandomizer(CompoundTag compoundTag) {
        this.defaultItem = BuiltInRegistries.ITEM.byId(compoundTag.getInt("defaultItemId"));
        this.entries = new ArrayList<>();

        ListTag entriesTag = compoundTag.getList("entries", 10);
        entriesTag.forEach(entryTag -> {
            CompoundTag entryCompoundTag = ((CompoundTag) entryTag);
            Item item = BuiltInRegistries.ITEM.byId(entryCompoundTag.getInt("entryItemId"));
            float chance = entryCompoundTag.getFloat("entryChance");
            this.addItem(item, chance);
        });
    }

    /**
     * Constructs a new ItemRandomizer from a list of Entries and a default Item.
     * @param entries List of Entries
     * @param defaultItem The default Item
     */
    public ItemRandomizer(List<Entry> entries, Item defaultItem) {
        this.entries = entries;
        this.defaultItem = defaultItem;
    }

    /**
     * Constructs a new ItemRandomizer with only a default Item.
     * @param defaultItem The default Item
     */
    public ItemRandomizer(Item defaultItem) {
        this.defaultItem = defaultItem;
    }

    /**
     * Constructs a new ItemRandomizer with no default Item nor entries.
     */
    public ItemRandomizer() {
    }

    /**
     * Convenience factory function to construct an ItemRandomizer from a list of Items.
     * Each Item will have equal probability of being chosen.
     */
    public static ItemRandomizer from(Item... items) {
        ItemRandomizer randomizer = new ItemRandomizer();
        float chance = 1f / items.length;

        for (Item item : items) {
            randomizer.addItem(item, chance);
        }

        return randomizer;
    }

    /**
     * Adds an Item with given chance of being selected.
     * @return The modified ItemRandomizer
     */
    public ItemRandomizer addItem(Item item, float chance) {
        // Abort if Item already a part of this randomizer
        if (entries.stream().anyMatch(entry -> entry.item.equals(item))) {
            YungsApiCommon.LOGGER.warn("WARNING: duplicate item {} added to ItemRandomizer!", item.toString());
            return this;
        }

        // Attempt to add Item to entries
        float currTotal = entries.stream().map(entry -> entry.probability).reduce(Float::sum).orElse(0f);
        float newTotal = currTotal + chance;
        if (newTotal > 1) { // Total probability cannot exceed 1
            YungsApiCommon.LOGGER.warn("WARNING: item {} added to ItemRandomizer exceeds max probabiltiy of 1!", item.toString());
            return this;
        }
        entries.add(new Entry(item, chance));
        return this;
    }

    /**
     * Randomly selects an Item from this ItemRandomizer.
     * The random provided should be one used in generation of your structure or feature,
     * to ensure reproducibility for the same world seed.
     */
    public Item get(Random random) {
        float target = random.nextFloat();
        float currBottom = 0;

        for (Entry entry : entries) {
            if (currBottom <= target && target < currBottom + entry.probability) {
                return entry.item;
            }

            currBottom += entry.probability;
        }

        // No match found
        return this.defaultItem;
    }

    /**
     * Randomly selects an Item from this ItemRandomizer.
     * The RandomSource provided should be one used in generation of your structure or feature,
     * to ensure reproducibility for the same world seed.
     */
    public Item get(RandomSource randomSource) {
        float target = randomSource.nextFloat();
        float currBottom = 0;

        for (Entry entry : entries) {
            if (currBottom <= target && target < currBottom + entry.probability) {
                return entry.item;
            }

            currBottom += entry.probability;
        }

        // No match found
        return this.defaultItem;
    }

    /**
     * Sets the default Item.
     * The default Item is used for any leftover probability ranges.
     */
    public void setDefaultItem(Item item) {
        this.defaultItem = item;
    }

    /**
     * Returns a Map of Items to their corresponding probabilities.
     * Does not include the default Item.
     * @return The Map
     */
    public Map<Item, Float> getEntriesAsMap() {
        Map<Item, Float> map = new HashMap<>();
        this.entries.forEach(entry -> map.put(entry.item, entry.probability));
        return map;
    }

    /**
     * Returns a List of Entries.
     * @return The List
     */
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * Returns the default Item.
     * @return The default Item
     */
    public Item getDefaultItem() {
        return defaultItem;
    }

    /**
     * Represents an Item and its corresponding probability of being chosen.
     */
    public static class Entry {
        public static Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter((entry) -> entry.item),
                        Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(entry -> entry.probability))
                .apply(instance, Entry::new));

        public Item item;
        public float probability;

        public Entry(Item item, float probability) {
            this.item = item;
            this.probability = probability;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Entry) {
                return this.item.equals(((Entry) obj).item);
            } else if (obj instanceof Item) {
                return this.item.equals(obj);
            }
            return false;
        }
    }
}