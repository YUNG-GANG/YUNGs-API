package com.yungnickyoung.minecraft.yungsapi.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yungnickyoung.minecraft.yungsapi.world.ItemSetSelector;
import net.minecraft.item.Item;

import java.io.IOException;
import java.util.Map;

/**
 * GSON TypeAdapter to serialize/deserialize {@link ItemSetSelector}.
 */
public class ItemSetSelectorAdapter extends TypeAdapter<ItemSetSelector> {
    public ItemSetSelector read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        ItemSetSelector selector = new ItemSetSelector();

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

    public void write(JsonWriter writer, ItemSetSelector selector) throws IOException {
        if (selector == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();

        // Entries map
        writer.name("entries").beginObject();
        for (Map.Entry<Item, Float> entry : selector.getEntries().entrySet()) {
            writer.name(String.valueOf(entry.getKey())).value(entry.getValue());
        }
        writer.endObject();

        // Default item
        String defaultItemString = String.valueOf(selector.getDefaultItem());
        writer.name("defaultItem").value(defaultItemString);

        writer.endObject();
    }
}