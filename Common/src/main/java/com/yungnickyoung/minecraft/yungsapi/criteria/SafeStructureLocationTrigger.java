package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

/**
 * Custom trigger for safely locating a structure.
 * Unlike vanilla, if the structure does not exist, then the trigger will simply fail
 * (as one would expect).
 *
 * @author TelepathicGrunt
 */
public class SafeStructureLocationTrigger extends SimpleCriterionTrigger<SafeStructureLocationTrigger.TriggerInstance> {
    private final ResourceLocation id;

    public SafeStructureLocationTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        JsonObject jsonobject = GsonHelper.getAsJsonObject(jsonObject, "location", jsonObject);
        SafeStructureLocationPredicate safeStructureLocationPredicate = SafeStructureLocationPredicate.fromJson(jsonobject);
        return new TriggerInstance(this.id, contextAwarePredicate, safeStructureLocationPredicate);
    }

    public void trigger(Player player) {
        if (player instanceof ServerPlayer) {
            this.trigger((ServerPlayer) player, (triggerInstance) ->
                    triggerInstance.matches(((ServerPlayer) player).serverLevel(), player.getX(), player.getY(), player.getZ()));
        }
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final SafeStructureLocationPredicate location;

        public TriggerInstance(ResourceLocation id, ContextAwarePredicate contextAwarePredicate, SafeStructureLocationPredicate locationPredicate) {
            super(id, contextAwarePredicate);
            this.location = locationPredicate;
        }

        public static TriggerInstance located(SafeStructureLocationPredicate safeStructureLocationPredicate) {
            return new TriggerInstance(CriteriaTriggers.LOCATION.getId(), ContextAwarePredicate.ANY, safeStructureLocationPredicate);
        }

        public boolean matches(ServerLevel serverLevel, double x, double y, double z) {
            return this.location.matches(serverLevel, x, y, z);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("location", this.location.serializeToJson());
            return jsonObject;
        }
    }
}