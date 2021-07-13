package com.yungnickyoung.minecraft.yungsapi.criteria;

import com.google.gson.JsonObject;
import com.yungnickyoung.minecraft.yungsapi.init.YAModCriteria;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * Custom trigger for safely locating a structure.
 * Unlike vanilla, if the structure does not exist, then the trigger will simply fail
 * (as one might expect).
 *
 * @author TelepathicGrunt
 */
public class SafeStructurePositionCriterion extends AbstractCriterion<SafeStructurePositionCriterion.Conditions> {
    private final Identifier id;

    public SafeStructurePositionCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject json, EntityPredicate.Extended entityPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        JsonObject jsonobject = JsonHelper.getObject(json, "location", json);
        SafeStructureLocatePredicate safeStructureLocatePredicate = SafeStructureLocatePredicate.fromJson(jsonobject);
        return new Conditions(this.id, entityPredicate, safeStructureLocatePredicate);
    }

    public void trigger(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            this.test((ServerPlayerEntity) player, (conditions) -> conditions.matches(((ServerPlayerEntity) player).getServerWorld(), player.getX(), player.getY(), player.getZ()));
        }
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final SafeStructureLocatePredicate location;

        public Conditions(Identifier id, EntityPredicate.Extended player, SafeStructureLocatePredicate location) {
            super(id, player);
            this.location = location;
        }

        public static Conditions forLocation(SafeStructureLocatePredicate location) {
            return new Conditions(Criteria.LOCATION.getId(), EntityPredicate.Extended.EMPTY, location);
        }

        public boolean matches(ServerWorld world, double x, double y, double z) {
            return this.location.test(world, x, y, z);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("location", this.location.toJson());
            return jsonObject;
        }
    }
}
