package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

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
        private float spawnDimensionsScale = 1.0F;
        private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
        private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

        private Builder(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
            this.factory = entityFactory;
            this.category = mobCategory;
            this.canSpawnFarFromPlayer = mobCategory == MobCategory.CREATURE || mobCategory == MobCategory.MISC;
        }

        public static <T extends Entity> Builder<T> of(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
            return new Builder<>(entityFactory, mobCategory);
        }

        public Builder<T> sized(float width, float height) {
            this.dimensions = EntityDimensions.scalable(width, height);
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

        public Builder<T> spawnDimensionsScale(float scale) {
            this.spawnDimensionsScale = scale;
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

        public EntityType<T> build() {
            return new EntityType<>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions, this.spawnDimensionsScale, this.clientTrackingRange, this.updateInterval, this.requiredFeatures);
        }
    }
}
