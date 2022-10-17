package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of EntityTypes.
 */
public class EntityTypeModuleForge {
    public static final Map<AutoRegisterEntityType<? extends LivingEntity>, Supplier<AttributeSupplier.Builder>> ENTITY_ATTRIBUTES = new HashMap<>();

    public static void processEntries() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, EntityTypeModuleForge::registerEntityTypes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EntityTypeModuleForge::registerEntityAttributes);
    }

    private static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        AutoRegistrationManager.ENTITY_TYPES.stream()
                .filter(data -> !data.processed())
                .forEach(data -> registerEntityType(data, event.getRegistry()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerEntityType(AutoRegisterField data, IForgeRegistry<EntityType<?>> registry) {
        AutoRegisterEntityType autoRegisterEntityType = (AutoRegisterEntityType) data.object();
        EntityType<?> entityType = (EntityType<?>) autoRegisterEntityType.get();
        entityType.setRegistryName(data.name());
        registry.register(entityType);

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
            builder.add(ForgeMod.SWIM_SPEED.get())
                    .add(ForgeMod.NAMETAG_DISTANCE.get())
                    .add(ForgeMod.ENTITY_GRAVITY.get());
            event.put(entityType.get(), builder.build());
        });
    }
}
