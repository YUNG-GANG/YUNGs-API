package com.yungnickyoung.minecraft.yungsapi.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GSON TypeAdapter to serialize/deserialize {@link BlockState}.
 */
@Deprecated
public class BlockStateAdapter extends TypeAdapter<BlockState> {
    public BlockState read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        return resolveBlockState(reader.nextString());
    }

    public void write(JsonWriter writer, BlockState blockState) throws IOException {
        if (blockState == null) {
            writer.nullValue();
            return;
        }

        String blockString = String.valueOf(blockState);
        if (blockString.startsWith("Block")) blockString = blockString.replace("Block", "");
        blockString = blockString.replace("{", "");
        blockString = blockString.replace("}", "");

        writer.value(blockString);
    }

    public static BlockState resolveBlockState(String fullString) {
        BlockState blockState;
        Map<String, String> properties = new HashMap<>();
        String blockString = fullString;

        int startIndex = fullString.indexOf('[');
        int stopIndex = fullString.indexOf(']');

        if (startIndex != -1) {
            blockString = fullString.substring(0, startIndex);
            if (stopIndex < startIndex) {
                YungsApiCommon.LOGGER.error("JSON: Malformed property {}. Missing a bracket?", fullString);
                YungsApiCommon.LOGGER.error("Using air instead...");
                return Blocks.AIR.defaultBlockState();
            }

            int index = startIndex + 1;
            String currKey = "";
            StringBuilder currString = new StringBuilder();

            while (index <= stopIndex) {
                char currChar = fullString.charAt(index);

                if (currChar == '=') {
                    currKey = currString.toString();
                    currString = new StringBuilder();
                } else if (currChar == ',' || currChar == ']') {
                    properties.put(currKey, currString.toString());
                    currString = new StringBuilder();
                } else {
                    currString.append(fullString.charAt(index));
                }

                index++;
            }
        }

        try {
            blockState = Registry.BLOCK.get(new ResourceLocation(blockString)).defaultBlockState();
        } catch (Exception e) {
            YungsApiCommon.LOGGER.error("JSON: Unable to read block '{}': {}", blockString, e.toString());
            YungsApiCommon.LOGGER.error("Using air instead...");
            return Blocks.AIR.defaultBlockState();
        }

        if (properties.size() > 0) {
            blockState = getConfiguredBlockState(blockState, properties);
        }

        return blockState;
    }

    /**
     * Attempts to parse the properties from the provided properties map and apply them to the provided blockstate
     * @param blockState Blockstate to apply properties to
     * @param properties Map of property names to property values
     * @param <T> The type of the property enum, usually resides within the Block's class
     * @return The configured blockstate
     */
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState getConfiguredBlockState(BlockState blockState, Map<String, String> properties) {
        // Convert string property name/val into actual properties I can apply to the blockstate
        Block block = blockState.getBlock();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            boolean found = false;

            for (Property<?> p : blockState.getProperties()) {
                Property<T> property = (Property<T>) p;
                if (property.getName().equals(key)) {
                    T val = property.getValue(value).orElse(null);
                    if (val == null) {
                        YungsApiCommon.LOGGER.error("JSON: Found null for property {} for block {}", property, Registry.BLOCK.getId(block));
                        continue;
                    }
                    blockState = blockState.setValue(property, val);
                    found = true;
                    break;
                }
            }

            if (!found) {
                YungsApiCommon.LOGGER.error("JSON: Unable to find property {} for block {}", key, Registry.BLOCK.getId(block));
            }
        }

        return blockState;
    }
}