package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class AutoRegisterFieldRouter {
    public static void queueField(AutoRegisterField registerData) {
        if (registerData.object() instanceof StructureType<?>) {
            AutoRegistrationManager.STRUCTURE_TYPES.add(registerData);
        } else if (registerData.object() instanceof StructurePoolElementType<?>) {
            AutoRegistrationManager.STRUCTURE_POOL_ELEMENT_TYPES.add(registerData);
        } else if (registerData.object() instanceof StructurePieceType) {
            AutoRegistrationManager.STRUCTURE_PIECE_TYPES.add(registerData);
        } else if (registerData.object() instanceof StructurePlacementType<?>) {
            AutoRegistrationManager.STRUCTURE_PLACEMENT_TYPES.add(registerData);
        } else if (registerData.object() instanceof Feature<?>) {
            AutoRegistrationManager.FEATURES.add(registerData);
        } else if (registerData.object() instanceof PlacementModifierType<?>) {
            AutoRegistrationManager.PLACEMENT_MODIFIER_TYPES.add(registerData);
        } else if (registerData.object() instanceof CriterionTrigger) {
            AutoRegistrationManager.CRITERION_TRIGGERS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterBlock) {
            AutoRegistrationManager.BLOCKS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterItem) {
            AutoRegistrationManager.ITEMS.add(registerData);
        } else if (registerData.object() instanceof StructureProcessorType<?>) {
            AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterBlockEntityType) {
            AutoRegistrationManager.BLOCK_ENTITY_TYPES.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterCreativeTab) {
            AutoRegistrationManager.CREATIVE_MODE_TABS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterSoundEvent) {
            AutoRegistrationManager.SOUND_EVENTS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterCommand) {
            AutoRegistrationManager.COMMANDS.add(registerData);
        }
    }
}