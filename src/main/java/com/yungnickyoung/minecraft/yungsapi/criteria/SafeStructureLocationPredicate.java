package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

/**
 * Custom predicate for safely locating a structure.
 * Unlike vanilla, if the structure does not exist, then the criteria will simply fail
 * (as one might expect).
 *
 * @author TelepathicGrunt
 */
public class SafeStructureLocationPredicate {
    private final StructureFeature<?> structure;

    public SafeStructureLocationPredicate(StructureFeature<?> structure) {
        this.structure = structure;
    }

    public boolean matches(ServerLevel serverLevel, double x, double y, double z) {
        return this.matches(serverLevel, (float)x, (float)y, (float)z);
    }

    public boolean matches(ServerLevel serverLevel, float x, float y, float z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        return this.structure != null &&
                serverLevel.isLoaded(blockpos) &&
                serverLevel.structureFeatureManager().getStructureWithPieceAt(blockpos, this.structure).isValid();
    }

    public JsonElement serializeToJson() {
        JsonObject jsonObject = new JsonObject();
        if (this.structure != null) {
            jsonObject.addProperty("feature", this.structure.getFeatureName());
        }
        return jsonObject;
    }

    public static SafeStructureLocationPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement != null && !jsonElement.isJsonNull()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "location");
            StructureFeature<?> structure = jsonObject.has("feature") ? Registry.STRUCTURE_FEATURE.get(new ResourceLocation(GsonHelper.getAsString(jsonObject, "feature"))) : null;

            return new SafeStructureLocationPredicate(structure);
        } else {
            return new SafeStructureLocationPredicate(null);
        }
    }
}