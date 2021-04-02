package com.yungnickyoung.minecraft.yungsapi.world;

import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Describes a set of Items and the probability of each Item in the set being chosen.
 * This is very useful for easily adding random variation to things like armor stands.
 */
public class ItemSetSelector {
    /**
     * Map of Items to their corresponding probabilities.
     * The total sum of all the probabilities should not exceed 1.
     */
    private Map<Item, Float> entries = new HashMap<>();

    /**
     * The default Item is used for any leftover probability ranges.
     * For example, if the total sum of all the probabilities of the entries is 0.6, then
     * there is a 0.4 chance of the defaultItem being selected.
     */
    private Item defaultItem = Items.AIR;

    public ItemSetSelector(Item defaultItem) {
        this.defaultItem = defaultItem;
    }

    public ItemSetSelector() {
    }

    /**
     * Convenience function to construct an ItemSetSelector from a list of Items.
     * Each Item will have equal probability of being chosen.
     */
    public static ItemSetSelector from(Item... items) {
        ItemSetSelector selector = new ItemSetSelector();
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
    public ItemSetSelector addItem(Item item, float chance) {
        // Abort if Item already a part of this selector
        if (entries.containsKey(item)) {
            YungsApi.LOGGER.warn(String.format("WARNING: duplicate item %s added to ItemSetSelector!", item.toString()));
            return this;
        }

        // Attempt to add Item to entries
        float currTotal = entries.values().stream().reduce(Float::sum).orElse(0f);
        float newTotal = currTotal + chance;
        if (newTotal > 1) { // Total probability cannot exceed 1
            YungsApi.LOGGER.warn(String.format("WARNING: item %s added to ItemSetSelector exceeds max probabiltiy of 1!", item.toString()));
            return this;
        }
        entries.put(item, chance);
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

        for (Map.Entry<Item, Float> entry : entries.entrySet()) {
            float chance = entry.getValue();
            if (currBottom <= target && target < currBottom + chance) {
                return entry.getKey();
            }

            currBottom += chance;
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

    public Map<Item, Float> getEntries() {
        return entries;
    }

    public Item getDefaultItem() {
        return defaultItem;
    }
}
