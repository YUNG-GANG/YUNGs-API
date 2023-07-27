package com.yungnickyoung.minecraft.yungsapi.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.ItemRandomizer;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.util.Map;

/**
 * GSON TypeAdapter to serialize/deserialize {@link ItemRandomizer}.
 */
public class ItemRandomizerAdapter extends TypeAdapter<ItemRandomizer> {
    public ItemRandomizer read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        ItemRandomizer randomizer = new ItemRandomizer();

        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "entries" -> {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        Item item = ItemAdapter.resolveItem(reader.nextName());
                        double probability = reader.nextDouble();
                        randomizer.addItem(item, (float) probability);
                    }
                    reader.endObject();
                }
                case "defaultItem" -> {
                    Item item = ItemAdapter.resolveItem(reader.nextString());
                    randomizer.setDefaultItem(item);
                }
            }
        }
        reader.endObject();
        return randomizer;
    }

    public void write(JsonWriter writer, ItemRandomizer randomizer) throws IOException {
        if (randomizer == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();

        // Entries map
        writer.name("entries").beginObject();
        for (Map.Entry<Item, Float> entry : randomizer.getEntriesAsMap().entrySet()) {
            writer.name(String.valueOf(entry.getKey())).value(entry.getValue());
        }
        writer.endObject();

        // Default item
        String defaultItemString = String.valueOf(randomizer.getDefaultItem());
        writer.name("defaultItem").value(defaultItemString);

        writer.endObject();
    }
}