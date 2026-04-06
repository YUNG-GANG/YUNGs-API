package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Wrapper for registering {@link EntityType}s with AutoRegister.
 * Note that you should use {@link AutoRegisterEntityType.Builder} and not the vanilla EntityType.Builder.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 * @AutoRegister("goblin")
 * public static final AutoRegisterEntityType<GoblinEntity> GOBLIN = AutoRegisterEntityType.of(() ->
 *         AutoRegisterEntityType.Builder
 *                 .of(GoblinEntity::new, MobCategory.MONSTER)
 *                 .sized(0.75f, 0.9f)
 *                 .build())
 *         .attributes(GoblinEntity::createAttributes);
 * }
 * </pre>
 */
public class AutoRegisterEntityType<T extends Entity> extends AutoRegisterEntry<EntityType<T>> {
    private Supplier<AttributeSupplier.Builder> attributesBuilderSupplier;

    public static <U extends Entity> AutoRegisterEntityType<U> of(Supplier<EntityType<U>> entityTypeSupplier) {
        return new AutoRegisterEntityType<>(entityTypeSupplier);
    }

    public AutoRegisterEntityType<T> attributes(Supplier<AttributeSupplier.Builder> attributesBuilderSupplier) {
        this.attributesBuilderSupplier = attributesBuilderSupplier;
        return this;
    }

    public boolean hasAttributes() {
        return this.attributesBuilderSupplier != null;
    }

    public Supplier<AttributeSupplier.Builder> getAttributesSupplier() {
        return this.attributesBuilderSupplier;
    }

    private AutoRegisterEntityType(Supplier<EntityType<T>> entityTypeSupplier) {
        super(entityTypeSupplier);
    }

    /**
     * Builder for creating AutoRegisterEntityTypes.
     * This directly mirrors the vanilla EntityType.Builder.
     */
    public static class Builder<T extends Entity> {
        private final EntityType.EntityFactory<T> factory;
        private final MobCategory category;
        private ImmutableSet<Block> immuneTo = ImmutableSet.of();
        private boolean serialize = true;
        private boolean summon = true;
        private boolean fireImmune;
        private boolean canSpawnFarFromPlayer;
        private int clientTrackingRange = 5;
        private int updateInterval = 3;
        private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
        private float spawnDimensionsScale = 1.0F;
        private EntityAttachments.Builder attachments = EntityAttachments.builder();
        private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
        private DependantName<EntityType<?>, String> descriptionId;
        private DependantName<EntityType<?>, Optional<ResourceKey<LootTable>>> lootTable;
        private boolean allowedInPeaceful;

        private Builder(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
            this.factory = entityFactory;
            this.category = mobCategory;
            this.canSpawnFarFromPlayer = mobCategory == MobCategory.CREATURE || mobCategory == MobCategory.MISC;
            this.lootTable = resourceKey -> Optional.of(ResourceKey.create(
                    Registries.LOOT_TABLE,
                    resourceKey.identifier().withPrefix("entities/")));
            this.descriptionId = resourceKey -> Util.makeDescriptionId("entity", resourceKey.identifier());
            this.allowedInPeaceful = true;
        }

        public static <T extends Entity> Builder<T> of(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
            return new Builder<>(entityFactory, mobCategory);
        }

        public Builder<T> sized(float width, float height) {
            this.dimensions = EntityDimensions.scalable(width, height);
            return this;
        }

        public Builder<T> spawnDimensionsScale(float scale) {
            this.spawnDimensionsScale = scale;
            return this;
        }

        public Builder<T> eyeHeight(float eyeHeight) {
            this.dimensions = this.dimensions.withEyeHeight(eyeHeight);
            return this;
        }

        public Builder<T> passengerAttachments(float... attachments) {
            for (float attachment : attachments) {
                this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, 0.0F, attachment, 0.0F);
            }

            return this;
        }

        public Builder<T> passengerAttachments(Vec3... attachments) {
            for (Vec3 attachment : attachments) {
                this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, attachment);
            }

            return this;
        }

        public Builder<T> vehicleAttachment(Vec3 attachment) {
            return this.attach(EntityAttachment.VEHICLE, attachment);
        }

        public Builder<T> ridingOffset(float offset) {
            return this.attach(EntityAttachment.VEHICLE, 0.0F, -offset, 0.0F);
        }

        public Builder<T> nameTagOffset(float offset) {
            return this.attach(EntityAttachment.NAME_TAG, 0.0F, offset, 0.0F);
        }

        public Builder<T> attach(EntityAttachment attachment, float x, float y, float z) {
            this.attachments = this.attachments.attach(attachment, x, y, z);
            return this;
        }

        public Builder<T> attach(EntityAttachment attachment, Vec3 attachPos) {
            this.attachments = this.attachments.attach(attachment, attachPos);
            return this;
        }

        public Builder<T> noSummon() {
            this.summon = false;
            return this;
        }

        public Builder<T> noSave() {
            this.serialize = false;
            return this;
        }

        public Builder<T> fireImmune() {
            this.fireImmune = true;
            return this;
        }

        public Builder<T> immuneTo(Block... blocks) {
            this.immuneTo = ImmutableSet.copyOf(blocks);
            return this;
        }

        public Builder<T> canSpawnFarFromPlayer() {
            this.canSpawnFarFromPlayer = true;
            return this;
        }

        public Builder<T> clientTrackingRange(int chunkRange) {
            this.clientTrackingRange = chunkRange;
            return this;
        }

        public Builder<T> updateInterval(int interval) {
            this.updateInterval = interval;
            return this;
        }

        public Builder<T> requiredFeatures(FeatureFlag... $$0) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset($$0);
            return this;
        }

        public Builder<T> noLootTable() {
            this.lootTable = DependantName.fixed(Optional.empty());
            return this;
        }

        public Builder<T> notInPeaceful() {
            this.allowedInPeaceful = false;
            return this;
        }

        public EntityType<T> build(ResourceKey<EntityType<?>> resourceKey) {
            return new EntityType<>(
                    this.factory,
                    this.category,
                    this.serialize,
                    this.summon,
                    this.fireImmune,
                    this.canSpawnFarFromPlayer,
                    this.immuneTo,
                    this.dimensions.withAttachments(this.attachments),
                    this.spawnDimensionsScale,
                    this.clientTrackingRange,
                    this.updateInterval,
                    this.descriptionId.get(resourceKey),
                    this.lootTable.get(resourceKey),
                    this.requiredFeatures,
                    this.allowedInPeaceful
            );
        }
    }
}
