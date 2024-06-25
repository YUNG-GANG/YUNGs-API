package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Specifies the type of a specific {@link StructureCondition}.
 * This class also serves as the registration hub for StructureConditions and their corresponding types.
 */
public interface StructureConditionType<C extends StructureCondition> {
    /* Utility maps for codecs. Simulates the approach vanilla registries use. */
    Map<ResourceLocation, StructureConditionType<?>> CONDITION_TYPES_BY_NAME = new HashMap<>();
    Map<StructureConditionType<?>, ResourceLocation> NAME_BY_CONDITION_TYPES = new HashMap<>();

    /* Codecs */
    Codec<StructureConditionType<?>> CONDITION_TYPE_CODEC = ResourceLocation.CODEC
            .flatXmap(
                    resourceLocation -> Optional.ofNullable(CONDITION_TYPES_BY_NAME.get(resourceLocation))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown condition type: " + resourceLocation)),
                    conditionType -> Optional.of(NAME_BY_CONDITION_TYPES.get(conditionType))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "No ID found for condition type " + conditionType + ". Is it registered?")));

    Codec<StructureCondition> CONDITION_CODEC = StructureConditionType.CONDITION_TYPE_CODEC
            .dispatch("type", StructureCondition::type, StructureConditionType::codec);

    /* Types. Add any new types here! */
    StructureConditionType<AlwaysTrueCondition> ALWAYS_TRUE = register("always_true", AlwaysTrueCondition.CODEC);
    StructureConditionType<AnyOfCondition> ANY_OF = register("any_of", AnyOfCondition.CODEC);
    StructureConditionType<AllOfCondition> ALL_OF = register("all_of", AllOfCondition.CODEC);
    StructureConditionType<NotCondition> NOT = register("not", NotCondition.CODEC);
    StructureConditionType<AltitudeCondition> ALTITUDE = register("altitude", AltitudeCondition.CODEC);
    StructureConditionType<DepthCondition> DEPTH = register("depth", DepthCondition.CODEC);
    StructureConditionType<RandomChanceCondition> RANDOM_CHANCE = register("random_chance", RandomChanceCondition.CODEC);
    StructureConditionType<PieceInRangeCondition> PIECE_IN_RANGE = register("piece_in_range", PieceInRangeCondition.CODEC);
    StructureConditionType<ModLoaderCondition> MOD_LOADER = register("mod_loader", ModLoaderCondition.CODEC);
    StructureConditionType<ModLoadedCondition> MOD_LOADED = register("mod_loaded", ModLoadedCondition.CODEC);
    StructureConditionType<PieceInHorizontalDirectionCondition> PIECE_IN_HORIZONTAL_DIRECTION = register("piece_in_horizontal_direction", PieceInHorizontalDirectionCondition.CODEC);
    StructureConditionType<RotationCondition> ROTATION = register("rotation", RotationCondition.CODEC);

    /**
     * Utility method for registering StructureConditionTypes.
     */
    static <C extends StructureCondition> StructureConditionType<C> register(ResourceLocation resourceLocation, MapCodec<C> codec) {
        StructureConditionType<C> conditionType = () -> codec;
        CONDITION_TYPES_BY_NAME.put(resourceLocation, conditionType);
        NAME_BY_CONDITION_TYPES.put(conditionType, resourceLocation);
        return conditionType;
    }

    /**
     * Private utility method for registering StructureConditionTypes native to YUNG's API.
     */
    private static <C extends StructureCondition> StructureConditionType<C> register(String id, MapCodec<C> codec) {
        return register(ResourceLocation.fromNamespaceAndPath(YungsApiCommon.MOD_ID, id), codec);
    }

    /**
     * Supplies the codec for the {@link StructureCondition} corresponding to this StructureConditionType.
     */
    MapCodec<C> codec();
}
