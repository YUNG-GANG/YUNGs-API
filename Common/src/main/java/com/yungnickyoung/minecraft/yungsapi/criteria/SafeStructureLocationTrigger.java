package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Custom trigger for safely locating a structure.
 * Unlike vanilla, if the structure does not exist, then the trigger will simply fail
 * (as one would expect).
 *
 * @author TelepathicGrunt
 */
public class SafeStructureLocationTrigger extends SimpleCriterionTrigger<SafeStructureLocationTrigger.TriggerInstance> {

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, Optional<ContextAwarePredicate> player, DeserializationContext deserializationContext) {
        JsonObject jsonobject = GsonHelper.getAsJsonObject(jsonObject, "location", jsonObject);
        SafeStructureLocationPredicate safeStructureLocationPredicate = SafeStructureLocationPredicate.fromJson(jsonobject);
        return new TriggerInstance(player, safeStructureLocationPredicate);
    }

    public void trigger(Player player) {
        if (player instanceof ServerPlayer) {
            this.trigger((ServerPlayer) player, (triggerInstance) ->
                    triggerInstance.matches(((ServerPlayer) player).serverLevel(), player.getX(), player.getY(), player.getZ()));
        }
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final SafeStructureLocationPredicate location;

        public TriggerInstance(Optional<ContextAwarePredicate> player, SafeStructureLocationPredicate locationPredicate) {
            super(player);
            this.location = locationPredicate;
        }

        public static TriggerInstance located(SafeStructureLocationPredicate safeStructureLocationPredicate) {
            return new TriggerInstance(Optional.empty(), safeStructureLocationPredicate);
        }

        public boolean matches(ServerLevel serverLevel, double x, double y, double z) {
            return this.location.matches(serverLevel, x, y, z);
        }

        @Override
        public @NotNull JsonObject serializeToJson() {
            JsonObject jsonObject = super.serializeToJson();
            jsonObject.add("location", this.location.serializeToJson());
            return jsonObject;
        }
    }
}