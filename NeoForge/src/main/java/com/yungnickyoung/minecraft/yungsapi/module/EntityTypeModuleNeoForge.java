package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of EntityTypes.
 */
public class EntityTypeModuleNeoForge {
    public static final Map<AutoRegisterEntityType<? extends LivingEntity>, Supplier<AttributeSupplier.Builder>> ENTITY_ATTRIBUTES = new HashMap<>();

    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildAutoRegistrar(Registries.ENTITY_TYPE, AutoRegistrationManager.ENTITY_TYPES, EntityTypeModuleNeoForge::buildEntityType));
        YungsApiNeoForge.loadingContextEventBus.addListener(EntityTypeModuleNeoForge::registerEntityAttributes);
    }

    private static EntityType<?> buildEntityType(AutoRegisterField data) {
        AutoRegisterEntityType autoRegisterEntityType = (AutoRegisterEntityType) data.object();
        EntityType<?> entityType = (EntityType<?>) autoRegisterEntityType.get();

        // Store attributes for registration, if attached
        if (autoRegisterEntityType.hasAttributes()) {
            ENTITY_ATTRIBUTES.put(autoRegisterEntityType, autoRegisterEntityType.getAttributesSupplier());
        }

        // Return for registering
        return entityType;
    }

    private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        ENTITY_ATTRIBUTES.forEach((entityType, builderSupplier) -> {
            AttributeSupplier.Builder builder = builderSupplier.get();
            // Attach required Forge attributes and register
            builder.add(NeoForgeMod.SWIM_SPEED.getDelegate())
                    .add(NeoForgeMod.NAMETAG_DISTANCE.getDelegate());
            event.put(entityType.get(), builder.build());
        });
    }
}
