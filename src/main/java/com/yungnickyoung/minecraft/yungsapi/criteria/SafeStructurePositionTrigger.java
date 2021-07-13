package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonObject;
import com.yungnickyoung.minecraft.yungsapi.init.YAModCriteria;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
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
public class SafeStructurePositionTrigger extends AbstractCriterionTrigger<SafeStructurePositionTrigger.Instance> {
    private final ResourceLocation id;

    public SafeStructurePositionTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayerEntity && event.player.ticksExisted % 20 == 0) {
            YAModCriteria.SAFE_STRUCTURE_POSITION_TRIGGER.trigger((ServerPlayerEntity) event.player);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public SafeStructurePositionTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        JsonObject jsonobject = JSONUtils.getJsonObject(json, "location", json);
        SafeStructureLocatePredicate safeStructureLocatePredicate = SafeStructureLocatePredicate.deserialize(jsonobject);
        return new SafeStructurePositionTrigger.Instance(this.id, entityPredicate, safeStructureLocatePredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        this.triggerListeners(player, (instance) -> instance.test(player.getServerWorld(), player.getPosX(), player.getPosY(), player.getPosZ()));
    }

    public static class Instance extends CriterionInstance {
        private final SafeStructureLocatePredicate location;

        public Instance(ResourceLocation id, EntityPredicate.AndPredicate player, SafeStructureLocatePredicate location) {
            super(id, player);
            this.location = location;
        }

        public static SafeStructurePositionTrigger.Instance forLocation(SafeStructureLocatePredicate location) {
            return new SafeStructurePositionTrigger.Instance(CriteriaTriggers.LOCATION.getId(), EntityPredicate.AndPredicate.ANY_AND, location);
        }

        public boolean test(ServerWorld world, double x, double y, double z) {
            return this.location.test(world, x, y, z);
        }

        @Override
        @ParametersAreNonnullByDefault
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("location", this.location.serialize());
            return jsonobject;
        }
    }
}

