package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterBlock;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterEntityType;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registration of EntityTypes.
 */
public class EntityTypeModuleForge {
    public static final Map<AutoRegisterEntityType<? extends LivingEntity>, Supplier<AttributeSupplier.Builder>> ENTITY_ATTRIBUTES = new HashMap<>();
    private static final Map<String, DeferredRegister<EntityType<?>>> registersByModId = new HashMap<>();

    public static void init() {
//        AutoRegistrationManager.ENTITY_TYPES.forEach(EntityTypeModuleForge::register);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, EntityTypeModuleForge::registerEntityTypes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EntityTypeModuleForge::registerEntityAttributes);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        AutoRegistrationManager.ENTITY_TYPES.forEach(data -> {
            AutoRegisterEntityType autoRegisterEntityType = (AutoRegisterEntityType) data.object();
            EntityType<?> entityType = (EntityType<?>) autoRegisterEntityType.get();
            entityType.setRegistryName(data.name());
            event.getRegistry().register(entityType);

            // Store attributes for registration, if attached
            if (autoRegisterEntityType.hasAttributes()) {
                ENTITY_ATTRIBUTES.put(autoRegisterEntityType, autoRegisterEntityType.getAttributesSupplier());
            }
        });
    }

//    @SuppressWarnings({"unchecked", "rawtypes"})
//    private static void register(AutoRegisterField data) {
//        // Create & register deferred registry for current mod, if necessary
//        String modId = data.name().getNamespace();
//        if (!registersByModId.containsKey(modId)) {
//            DeferredRegister<EntityType<?>> deferredRegister = DeferredRegister.create(ForgeRegistries.ENTITIES, modId);
//            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
//            registersByModId.put(modId, deferredRegister);
//        }
//
//        AutoRegisterEntityType autoRegisterEntityType = (AutoRegisterEntityType) data.object();
//        Supplier<EntityType<?>> entityTypeSupplier = autoRegisterEntityType.getSupplier();
//
//        // Register
//        DeferredRegister<EntityType<?>> deferredRegister = registersByModId.get(modId);
//        RegistryObject<EntityType<?>> registryObject = deferredRegister.register(data.name().getPath(), entityTypeSupplier);
//
//        // Update the supplier to use the RegistryObject so that it will be properly updated later on
//        autoRegisterEntityType.setSupplier(registryObject);
//
//        // Store attributes for registration, if attached
//        if (autoRegisterEntityType.hasAttributes()) {
//            ENTITY_ATTRIBUTES.put(autoRegisterEntityType, autoRegisterEntityType.getAttributesSupplier());
//        }
//    }

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
