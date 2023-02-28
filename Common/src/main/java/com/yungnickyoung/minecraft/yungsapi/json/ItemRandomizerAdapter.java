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

        ItemRandomizer selector = new ItemRandomizer();

        reader.beginObject();
        while (reader.hasNext()) {
            switch(reader.nextName()) {
                case "entries":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        Item item = ItemAdapter.resolveItem(reader.nextName());
                        double probability = reader.nextDouble();
                        selector.addItem(item, (float) probability);
                    }
                    reader.endObject();
                    break;
                case "defaultItem":
                    Item item = ItemAdapter.resolveItem(reader.nextString());
                    selector.setDefaultItem(item);
                    break;
            }
        }
        reader.endObject();
        return selector;
    }

    public void write(JsonWriter writer, ItemRandomizer selector) throws IOException {
        if (selector == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();

        // Entries map
        writer.name("entries").beginObject();
        for (Map.Entry<Item, Float> entry : selector.getEntriesAsMap().entrySet()) {
            writer.name(String.valueOf(entry.getKey())).value(entry.getValue());
        }
        writer.endObject();

        // Default item
        String defaultItemString = String.valueOf(selector.getDefaultItem());
        writer.name("defaultItem").value(defaultItemString);

        writer.endObject();
    }
}