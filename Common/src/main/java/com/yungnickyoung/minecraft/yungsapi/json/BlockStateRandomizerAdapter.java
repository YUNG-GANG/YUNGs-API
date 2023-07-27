package com.yungnickyoung.minecraft.yungsapi.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.util.Map;

/**
 * GSON TypeAdapter to serialize/deserialize {@link BlockStateRandomizer}.
 */
public class BlockStateRandomizerAdapter extends TypeAdapter<BlockStateRandomizer> {
    public BlockStateRandomizer read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        BlockStateRandomizer randomizer = new BlockStateRandomizer();

        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "entries" -> {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        BlockState blockState = BlockStateAdapter.resolveBlockState(reader.nextName());
                        double probability = reader.nextDouble();
                        randomizer.addBlock(blockState, (float) probability);
                    }
                    reader.endObject();
                }
                case "defaultBlock" -> {
                    BlockState blockState = BlockStateAdapter.resolveBlockState(reader.nextString());
                    randomizer.setDefaultBlockState(blockState);
                }
            }
        }
        reader.endObject();
        return randomizer;
    }

    public void write(JsonWriter writer, BlockStateRandomizer randomizer) throws IOException {
        if (randomizer == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();

        // Entries map
        writer.name("entries").beginObject();
        for (Map.Entry<BlockState, Float> entry : randomizer.getEntriesAsMap().entrySet()) {
            writer.name(trimmedBlockName(String.valueOf(entry.getKey()))).value(entry.getValue());
        }
        writer.endObject();

        // Default block
        String defaultBlockString = String.valueOf(randomizer.getDefaultBlockState());
        defaultBlockString = trimmedBlockName(defaultBlockString);
        writer.name("defaultBlock").value(defaultBlockString);

        writer.endObject();
    }

    /**
     * Removes the "Block{}" wrapper added in the BlockState's toString()
     */
    private String trimmedBlockName(String blockString) {
        if (blockString.startsWith("Block")) blockString = blockString.substring(5);
        blockString = blockString.replace("{", "");
        blockString = blockString.replace("}", "");
        return blockString;
    }
}
