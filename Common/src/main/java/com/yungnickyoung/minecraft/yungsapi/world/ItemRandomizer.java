package com.yungnickyoung.minecraft.yungsapi.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.*;

/**
 * Describes a set of Items and the probability of each Item in the set being chosen.
 * This is very useful for easily adding random variation to things like armor stands.
 */
public class ItemRandomizer {
    public static final Codec<ItemRandomizer> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    Entry.CODEC.listOf().fieldOf("entries").forGetter((randomizer) -> randomizer.entries),
                    Registry.ITEM.byNameCodec().fieldOf("defaultItem").forGetter((randomizer) -> randomizer.defaultItem))
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

    public CompoundTag saveTag() {
        CompoundTag compoundTag = new CompoundTag();

        // Save default blockstate
        compoundTag.putInt("defaultItemId", Registry.ITEM.getId(this.defaultItem));

        // Save entries
        ListTag entriesTag = Util.make(new ListTag(), (tag) -> {
            this.entries.forEach((entry) -> {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putInt("entryItemId", Registry.ITEM.getId(entry.item));
                entryTag.putFloat("entryChance", entry.probability);
                tag.add(entryTag);
            });
        });
        compoundTag.put("entries", entriesTag);

        return compoundTag;
    }

    public ItemRandomizer(CompoundTag compoundTag) {
        this.defaultItem = Registry.ITEM.byId(compoundTag.getInt("defaultItemId"));
        this.entries = new ArrayList<>();

        ListTag entriesTag = compoundTag.getList("entries", 10);
        entriesTag.forEach(entryTag -> {
            CompoundTag entryCompoundTag = ((CompoundTag) entryTag);
            Item item = Registry.ITEM.byId(entryCompoundTag.getInt("entryItemId"));
            float chance = entryCompoundTag.getFloat("entryChance");
            this.addItem(item, chance);
        });
    }

    public ItemRandomizer(List<Entry> entries, Item defaultItem) {
        this.entries = entries;
        this.defaultItem = defaultItem;
    }

    public ItemRandomizer(Item defaultItem) {
        this.defaultItem = defaultItem;
    }

    public ItemRandomizer() {
    }

    /**
     * Convenience function to construct an ItemSetSelector from a list of Items.
     * Each Item will have equal probability of being chosen.
     */
    public static ItemRandomizer from(Item... items) {
        ItemRandomizer selector = new ItemRandomizer();
        float chance = 1f / items.length;

        for (Item item : items) {
            selector.addItem(item, chance);
        }

        return selector;
    }

    /**
     * Add an Item with given chance of being selected.
     * @return The modified ItemSetSelector
     */
    public ItemRandomizer addItem(Item item, float chance) {
        // Abort if Item already a part of this selector
        if (entries.stream().anyMatch(entry -> entry.item.equals(item))) {
            YungsApiCommon.LOGGER.warn("WARNING: duplicate item {} added to ItemSetSelector!", item.toString());
            return this;
        }

        // Attempt to add Item to entries
        float currTotal = entries.stream().map(entry -> entry.probability).reduce(Float::sum).orElse(0f);
        float newTotal = currTotal + chance;
        if (newTotal > 1) { // Total probability cannot exceed 1
            YungsApiCommon.LOGGER.warn("WARNING: item {} added to ItemSetSelector exceeds max probabiltiy of 1!", item.toString());
            return this;
        }
        entries.add(new Entry(item, chance));
        return this;
    }

    /**
     * Randomly select an Item from this ItemSetSelector.
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
     * Randomly select an Item from this ItemSetSelector.
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
     * Sets the default Item for this selector.
     * The default Item is used for any leftover probability ranges.
     */
    public void setDefaultItem(Item item) {
        this.defaultItem = item;
    }

    public Map<Item, Float> getEntriesAsMap() {
        Map<Item, Float> map = new HashMap<>();
        this.entries.forEach(entry -> map.put(entry.item, entry.probability));
        return map;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Item getDefaultItem() {
        return defaultItem;
    }

    public static class Entry {
        public static Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        Registry.ITEM.byNameCodec().fieldOf("item").forGetter((entry) -> entry.item),
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