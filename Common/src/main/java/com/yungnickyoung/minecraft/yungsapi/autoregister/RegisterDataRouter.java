package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlockEntityType;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCreativeTab;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterItem;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class RegisterDataRouter {
    public static void queueRegisterData(RegisterData registerData) {
        if (registerData.object() instanceof StructureFeature<?>) {
            AutoRegistrationManager.STRUCTURE_FEATURES.add(registerData);
        } else if (registerData.object() instanceof StructurePoolElementType<?>) {
            AutoRegistrationManager.STRUCTURE_POOL_ELEMENT_TYPES.add(registerData);
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
        }
    }
}
