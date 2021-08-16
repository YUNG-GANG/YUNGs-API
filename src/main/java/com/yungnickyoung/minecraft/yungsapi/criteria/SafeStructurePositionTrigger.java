package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonObject;
import com.yungnickyoung.minecraft.yungsapi.init.YAModCriteria;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.event.TickEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Custom trigger for safely locating a structure.
 * Unlike vanilla, if the structure does not exist, then the trigger will simply fail
 * (as one might expect).
 *
 * @author TelepathicGrunt
 */
@MethodsReturnNonnullByDefault
public class SafeStructurePositionTrigger extends SimpleCriterionTrigger<SafeStructurePositionTrigger.Instance> {
    private final ResourceLocation id;

    public SafeStructurePositionTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer && event.player.tickCount % 20 == 0) {
            YAModCriteria.SAFE_STRUCTURE_POSITION_TRIGGER.trigger((ServerPlayer) event.player);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public SafeStructurePositionTrigger.Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext deserializationContext) {
        JsonObject jsonobject = GsonHelper.getAsJsonObject(json, "location", json);
        SafeStructureLocatePredicate safeStructureLocatePredicate = SafeStructureLocatePredicate.deserialize(jsonobject);
        return new SafeStructurePositionTrigger.Instance(this.id, entityPredicate, safeStructureLocatePredicate);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> instance.test(player.getLevel(), player.getX(), player.getY(), player.getZ()));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final SafeStructureLocatePredicate location;

        public Instance(ResourceLocation id, EntityPredicate.Composite player, SafeStructureLocatePredicate location) {
            super(id, player);
            this.location = location;
        }

        public static SafeStructurePositionTrigger.Instance located(SafeStructureLocatePredicate location) {
            return new SafeStructurePositionTrigger.Instance(CriteriaTriggers.LOCATION.getId(), EntityPredicate.Composite.ANY, location);
        }

        public boolean test(ServerLevel world, double x, double y, double z) {
            return this.location.test(world, x, y, z);
        }

        @Override
        @ParametersAreNonnullByDefault
        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            jsonobject.add("location", this.location.serialize());
            return jsonobject;
        }
    }
}

