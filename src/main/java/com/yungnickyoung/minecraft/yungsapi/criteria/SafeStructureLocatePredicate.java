package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * Custom predicate for safely locating a structure.
 * Unlike vanilla, if the structure does not exist, then the criteria will simply fail
 * (as one might expect).
 *
 * @author TelepathicGrunt
 */
public class SafeStructureLocatePredicate {
    @Nullable
    private final Structure<?> structure;

    public SafeStructureLocatePredicate(@Nullable Structure<?> structure) {
        this.structure = structure;
    }

    public boolean test(ServerWorld world, double x, double y, double z) {
        return this.test(world, (float)x, (float)y, (float)z);
    }

    public boolean test(ServerWorld world, float x, float y, float z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        return this.structure != null && world.isBlockPresent(blockpos) && world.func_241112_a_().getStructureStart(blockpos, true, this.structure).isValid();
    }

    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
        if (this.structure != null) {
            jsonobject.addProperty("feature", this.structure.getStructureName());
        }
        return jsonobject;
    }

    public static SafeStructureLocatePredicate deserialize(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "location");
            Structure<?> structure = jsonobject.has("feature") ? ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(JSONUtils.getString(jsonobject, "feature"))) : null;

            return new SafeStructureLocatePredicate(structure);
        } else {
            return new SafeStructureLocatePredicate(null);
        }
    }
}
