package com.yungnickyoung.minecraft.yungsapi.world.structure.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Specifies the type of a specific {@link StructureAction}.
 * This class also serves as the registration hub for StructureAction and their corresponding types.
 */
public interface StructureActionType<C extends StructureAction> {
    /* Utility maps for codecs. Simulates the approach vanilla registries use. */
    Map<ResourceLocation, StructureActionType<?>> ACTION_TYPES_BY_NAME = new HashMap<>();
    Map<StructureActionType<?>, ResourceLocation> NAME_BY_ACTION_TYPES = new HashMap<>();

    /* Codecs */
    Codec<StructureActionType<?>> ACTION_TYPE_CODEC = ResourceLocation.CODEC
            .flatXmap(
                    resourceLocation -> Optional.ofNullable(ACTION_TYPES_BY_NAME.get(resourceLocation))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown structure action type: " + resourceLocation)),
                    actionType -> Optional.of(NAME_BY_ACTION_TYPES.get(actionType))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "No ID found for structure action type " + actionType + ". Is it registered?")));

    Codec<StructureAction> ACTION_CODEC = StructureActionType.ACTION_TYPE_CODEC
            .dispatch("type", StructureAction::type, StructureActionType::codec);

    /* Types. Add any new types here! */
    StructureActionType<TransformAction> TRANSFORM = register("transform", TransformAction.CODEC);
    StructureActionType<DelayGenerationAction> DELAY_GENERATION = register("delay_generation", DelayGenerationAction.CODEC);

    /**
     * Utility method for registering StructureActionTypes.
     */
    static <C extends StructureAction> StructureActionType<C> register(ResourceLocation resourceLocation, MapCodec<C> codec) {
        StructureActionType<C> actionType = () -> codec;
        ACTION_TYPES_BY_NAME.put(resourceLocation, actionType);
        NAME_BY_ACTION_TYPES.put(actionType, resourceLocation);
        return actionType;
    }

    /**
     * Private utility method for registering StructureActionTypes native to YUNG's API.
     */
    private static <C extends StructureAction> StructureActionType<C> register(String id, MapCodec<C> codec) {
        return register(ResourceLocation.fromNamespaceAndPath(YungsApiCommon.MOD_ID, id), codec);
    }

    /**
     * Supplies the codec for the {@link StructureAction} corresponding to this StructureActionTypes.
     */
    MapCodec<C> codec();
}
