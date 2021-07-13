package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

/**
 * Custom predicate for safely locating a structure.
 * Unlike vanilla, if the structure does not exist, then the criteria will simply fail
 * (as one might expect).
 *
 * @author TelepathicGrunt
 */
public class SafeStructureLocatePredicate {
    private final StructureFeature<?> structure;

    public SafeStructureLocatePredicate(StructureFeature<?> structure) {
        this.structure = structure;
    }

    public boolean test(ServerWorld world, double x, double y, double z) {
        return this.test(world, (float)x, (float)y, (float)z);
    }

    public boolean test(ServerWorld world, float x, float y, float z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        return this.structure != null && world.canSetBlock(blockpos) && world.getStructureAccessor().getStructureAt(blockpos, true, this.structure).hasChildren();
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        if (this.structure != null) {
            jsonObject.addProperty("feature", this.structure.getName());
        }
        return jsonObject;
    }

    public static SafeStructureLocatePredicate fromJson(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            JsonObject jsonObject = JsonHelper.asObject(json, "location");
            StructureFeature<?> structure = jsonObject.has("feature") ? Registry.STRUCTURE_FEATURE.get(new Identifier(JsonHelper.getString(jsonObject, "feature"))) : null;

            return new SafeStructureLocatePredicate(structure);
        } else {
            return new SafeStructureLocatePredicate(null);
        }
    }
}