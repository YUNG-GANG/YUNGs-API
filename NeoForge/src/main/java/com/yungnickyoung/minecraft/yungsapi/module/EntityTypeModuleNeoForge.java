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
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of EntityTypes.
 */
public class EntityTypeModuleNeoForge {
    public static final Map<AutoRegisterEntityType<? extends LivingEntity>, Supplier<AttributeSupplier.Builder>> ENTITY_ATTRIBUTES = new HashMap<>();

    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(EntityTypeModuleNeoForge::registerEntityTypes);
        YungsApiNeoForge.loadingContextEventBus.addListener(EntityTypeModuleNeoForge::registerEntityAttributes);
    }

    private static void registerEntityTypes(final RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, helper -> {
            AutoRegistrationManager.ENTITY_TYPES.stream()
                    .filter(data -> !data.processed())
                    .forEach(data -> registerEntityType(data, helper));
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerEntityType(AutoRegisterField data, RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        AutoRegisterEntityType autoRegisterEntityType = (AutoRegisterEntityType) data.object();
        EntityType<?> entityType = (EntityType<?>) autoRegisterEntityType.get();
        helper.register(data.name(), entityType);

        // Store attributes for registration, if attached
        if (autoRegisterEntityType.hasAttributes()) {
            ENTITY_ATTRIBUTES.put(autoRegisterEntityType, autoRegisterEntityType.getAttributesSupplier());
        }

        data.markProcessed();
    }

    private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        ENTITY_ATTRIBUTES.forEach((entityType, builderSupplier) -> {
            AttributeSupplier.Builder builder = builderSupplier.get();
            // Attach required Forge attributes and register
            builder.add(NeoForgeMod.SWIM_SPEED.value())
                    .add(NeoForgeMod.NAMETAG_DISTANCE.value())
                    .add(NeoForgeMod.ENTITY_GRAVITY.value());
            event.put(entityType.get(), builder.build());
        });
    }
}
