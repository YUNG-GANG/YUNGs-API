package com.yungnickyoung.minecraft.yungsapi.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.io.IOException;

/**
 * GSON TypeAdapter to serialize/deserialize {@link Item}.
 */
public class ItemAdapter extends TypeAdapter<Item> {
    public Item read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        return resolveItem(reader.nextString());
    }

    public void write(JsonWriter writer, Item item) throws IOException {
        if (item == null) {
            writer.nullValue();
            return;
        }

        String itemString = String.valueOf(item);
        writer.value(itemString);
    }

    public static Item resolveItem(String itemString) {
        Item item;
        try {
            item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemString));
        } catch (Exception e) {
            YungsApiCommon.LOGGER.error("JSON: Unable to read item '{}': {}", itemString, e.toString());
            YungsApiCommon.LOGGER.error("Using air instead...");
            return Items.AIR;
        }

        return item;
    }
}
