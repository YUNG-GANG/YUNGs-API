package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/**
 * Registration of EntityTypes.
 */
public class EntityTypeModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.ENTITY_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(EntityTypeModuleFabric::register);
    }

    @SuppressWarnings("all")
    private static void register(AutoRegisterField data) {
        AutoRegisterEntityType<?> autoRegisterEntityType = (AutoRegisterEntityType<?>) data.object();
        EntityType<?> entityType = autoRegisterEntityType.get();

        // Register entity type
        Registry.register(BuiltInRegistries.ENTITY_TYPE, data.name(), entityType);

        // Register entity attributes, if attached
        if (autoRegisterEntityType.hasAttributes()) {
            AttributeSupplier.Builder attributesBuilder = autoRegisterEntityType.getAttributesSupplier().get();
            FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) entityType, attributesBuilder);
        }

        data.markProcessed();
    }
}
