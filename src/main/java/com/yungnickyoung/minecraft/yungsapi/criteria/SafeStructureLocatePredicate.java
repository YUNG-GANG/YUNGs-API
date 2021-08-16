package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
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
    private final StructureFeature<?> structure;

    public SafeStructureLocatePredicate(@Nullable StructureFeature<?> structure) {
        this.structure = structure;
    }

    public boolean test(ServerLevel world, double x, double y, double z) {
        return this.test(world, (float)x, (float)y, (float)z);
    }

    public boolean test(ServerLevel world, float x, float y, float z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        return this.structure != null && world.isLoaded(blockpos) && world.structureFeatureManager().getStructureAt(blockpos, true, this.structure).isValid();
    }

    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
        if (this.structure != null) {
            jsonobject.addProperty("feature", this.structure.getFeatureName());
        }
        return jsonobject;
    }

    public static SafeStructureLocatePredicate deserialize(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(element, "location");
            StructureFeature<?> structure = jsonobject.has("feature") ? ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(GsonHelper.getAsString(jsonobject, "feature"))) : null;

            return new SafeStructureLocatePredicate(structure);
        } else {
            return new SafeStructureLocatePredicate(null);
        }
    }
}
