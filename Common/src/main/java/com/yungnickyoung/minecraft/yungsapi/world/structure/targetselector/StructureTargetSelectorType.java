package com.yungnickyoung.minecraft.yungsapi.world.structure.targetselector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Specifies the type of a specific {@link StructureTargetSelector}.
 * This class also serves as the registration hub for TargetSelectors and their corresponding types.
 */
public interface StructureTargetSelectorType<C extends StructureTargetSelector> {
    /* Utility maps for codecs. Simulates the approach vanilla registries use. */
    Map<ResourceLocation, StructureTargetSelectorType<?>> TARGET_SELECTOR_TYPES_BY_NAME = new HashMap<>();
    Map<StructureTargetSelectorType<?>, ResourceLocation> NAME_BY_TARGET_SELECTOR_TYPES = new HashMap<>();

    /* Codecs */
    Codec<StructureTargetSelectorType<?>> TARGET_SELECTOR_TYPE_CODEC = ResourceLocation.CODEC
            .flatXmap(
                    resourceLocation -> Optional.ofNullable(TARGET_SELECTOR_TYPES_BY_NAME.get(resourceLocation))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown target selector type: " + resourceLocation)),
                    targetSelectorType -> Optional.of(NAME_BY_TARGET_SELECTOR_TYPES.get(targetSelectorType))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "No ID found for target selector type " + targetSelectorType + ". Is it registered?")));

    Codec<StructureTargetSelector> TARGET_SELECTOR_CODEC = StructureTargetSelectorType.TARGET_SELECTOR_TYPE_CODEC
            .dispatch("type", StructureTargetSelector::type, StructureTargetSelectorType::codec);

    /* Types. Add any new types here! */
    StructureTargetSelectorType<SelfTargetSelector> SELF = register("self", SelfTargetSelector.CODEC);

    /**
     * Utility method for registering TargetSelectorTypes.
     */
    static <C extends StructureTargetSelector> StructureTargetSelectorType<C> register(ResourceLocation resourceLocation, Codec<C> codec) {
        StructureTargetSelectorType<C> targetSelectorType = () -> codec;
        TARGET_SELECTOR_TYPES_BY_NAME.put(resourceLocation, targetSelectorType);
        NAME_BY_TARGET_SELECTOR_TYPES.put(targetSelectorType, resourceLocation);
        return targetSelectorType;
    }

    /**
     * Private utility method for registering TargetSelectorTypes native to YUNG's API.
     */
    private static <C extends StructureTargetSelector> StructureTargetSelectorType<C> register(String id, Codec<C> codec) {
        return register(new ResourceLocation(YungsApiCommon.MOD_ID, id), codec);
    }

    /**
     * Supplies the codec for the {@link StructureTargetSelector} corresponding to this TargetSelectorTypes.
     */
    Codec<C> codec();
}
