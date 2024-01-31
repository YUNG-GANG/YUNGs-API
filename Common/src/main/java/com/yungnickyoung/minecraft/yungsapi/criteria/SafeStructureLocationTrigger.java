//package com.yungnickyoung.minecraft.yungsapi.criteria;
//
//import com.google.gson.JsonObject;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.advancements.CriteriaTriggers;
//import net.minecraft.advancements.critereon.*;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.util.ExtraCodecs;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.entity.player.Player;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Optional;
//
///**
// * Custom trigger for safely locating a structure.
// * Unlike vanilla, if the structure does not exist, then the trigger will simply fail
// * (as one would expect).
// *
// * @author TelepathicGrunt
// */
//public class SafeStructureLocationTrigger extends SimpleCriterionTrigger<SafeStructureLocationTrigger.TriggerInstance> {
//    public SafeStructureLocationTrigger(ResourceLocation id) {
//    }
//
//    public @NotNull Codec<TriggerInstance> codec() {
//        return TriggerInstance.CODEC;
//    }
//
////    @Override
////    public TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
////        JsonObject jsonobject = GsonHelper.getAsJsonObject(jsonObject, "location", jsonObject);
////        SafeStructureLocationPredicate safeStructureLocationPredicate = SafeStructureLocationPredicate.fromJson(jsonobject);
////        return new TriggerInstance(this.id, contextAwarePredicate, safeStructureLocationPredicate);
////    }
//
//    public void trigger(Player player) {
//        if (player instanceof ServerPlayer) {
//            this.trigger((ServerPlayer) player, (triggerInstance) ->
//                    triggerInstance.matches(((ServerPlayer) player).serverLevel(), player.getX(), player.getY(), player.getZ()));
//        }
//    }
//
//    public static class TriggerInstance implements SimpleCriterionTrigger.SimpleInstance {
//        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance
//                .group(
//                        ResourceLocation.CODEC.fieldOf("location").forGetter(trigger -> trigger.location))
//                .apply(instance, TriggerInstance::new));
//
//        public final ResourceLocation location;
//
//        public TriggerInstance(ResourceLocation location) {
//            this.location = location;
//        }
//
//        public boolean matches(ServerLevel serverLevel, double x, double y, double z) {
//            return this.location.matches(serverLevel, x, y, z);
//        }
//
//        @Override
//        public JsonObject serializeToJson(SerializationContext serializationContext) {
//            JsonObject jsonObject = super.serializeToJson(serializationContext);
//            jsonObject.add("location", this.location.serializeToJson());
//            return jsonObject;
//        }
//
//        @Override
//        public Optional<ContextAwarePredicate> player() {
//            return Optional.empty();
//        }
//    }
//}