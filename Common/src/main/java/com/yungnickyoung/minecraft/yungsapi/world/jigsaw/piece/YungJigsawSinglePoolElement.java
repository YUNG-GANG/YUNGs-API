package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.condition.AllOfCondition;
import com.yungnickyoung.minecraft.yungsapi.world.condition.AnyOfCondition;
import com.yungnickyoung.minecraft.yungsapi.world.condition.DepthCondition;
import com.yungnickyoung.minecraft.yungsapi.world.condition.StructureCondition;
import com.yungnickyoung.minecraft.yungsapi.world.condition.StructureConditionType;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.modifier.StructureModifier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom {@link SinglePoolElement} with support for many additional settings.
 */
public class YungJigsawSinglePoolElement extends SinglePoolElement {
    public static final Codec<YungJigsawSinglePoolElement> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                    templateCodec(),
                    processorsCodec(),
                    projectionCodec(),
                    Codec.STRING.optionalFieldOf("name").forGetter(element -> element.name),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_count").forGetter(element -> element.maxCount),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_required_depth").forGetter(element -> element.minRequiredDepth),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_possible_depth").forGetter(element -> element.maxPossibleDepth),
                    Codec.BOOL.optionalFieldOf("is_priority", false).forGetter(element -> element.isPriority),
                    Codec.BOOL.optionalFieldOf("ignore_bounds", false).forGetter(element -> element.ignoreBounds),
                    StructureConditionType.CONDITION_CODEC.optionalFieldOf("condition", StructureCondition.ALWAYS_TRUE).forGetter(element -> element.condition),
//                    EnhancedTerrainAdaptationType.ADAPTATION_CODEC.optionalFieldOf("enhanced_terrain_adaptation").forGetter(element -> element.enhancedTerrainAdaptation),
                    ResourceLocation.CODEC.optionalFieldOf("deadend_pool").forGetter(element -> element.deadendPool),
                    StructureModifier.CODEC.listOf().optionalFieldOf("modifiers", new ArrayList<>()).forGetter(element -> element.modifiers)
            ).apply(builder, YungJigsawSinglePoolElement::new));

    /**
     * The name of this piece.
     * Solely used to uniquely identify pieces for the sake of tracking max_count.
     */
    public final Optional<String> name;

    /**
     * The maximum possible number of pieces with this name.
     * If this setting is used, the 'name' MUST be specified.
     */
    public final Optional<Integer> maxCount;

    /**
     * The minimum required depth (in jigsaw pieces) from the structure start
     * at which this piece can spawn.
     *
     * @deprecated Use {@link DepthCondition} instead.
     */
    @Deprecated
    public final Optional<Integer> minRequiredDepth;

    /**
     * The maximum allowed depth (in jigsaw pieces) from the structure start
     * at which this piece will no longer spawn.
     *
     * @deprecated Use {@link DepthCondition} instead.
     */
    @Deprecated
    public final Optional<Integer> maxPossibleDepth;

    /**
     * Whether this is a priority piece.
     * Priority pieces attempt to generate before all other pieces in the pool,
     * regardless of weight.
     */
    public final boolean isPriority;

    /**
     * Whether this piece should ignore the usual piece boundary checks.
     * Enabling this allows this piece to spawn while overlapping other pieces.
     */
    public final boolean ignoreBounds;

    /**
     * Optional condition required for this piece to spawn.
     * Can be any {@link StructureCondition}, including compound conditions
     * ({@link AnyOfCondition}, {@link AllOfCondition})
     */
    public final StructureCondition condition;

    /**
     * Optional enhanced terrain adaptation specific to this piece.
     * Takes priority over the structure's enhanced terrain adaptation if specified.
     */
//    public final Optional<EnhancedTerrainAdaptation> enhancedTerrainAdaptation;

    /**
     * Whether this piece should apply dead end adjustments.
     * If enabled, this piece has the possibility of being converted into one of its fallback pieces under certain conditions:
     * <ul>
     *     <li>This piece has one or more jigsaw blocks in addition to the one required to connect to its parent piece.</li>
     *     <li>This piece is not able to generate any other pieces from itself, including encased pieces.
     *     This could be due to either overlap with other pieces or reaching the max depth.</li>
     * </ul>
     * If both of these conditions are met, then a piece marked for deadend adjustments will be replaced with
     * an appropriate piece from its fallback pool instead.
     * <br />
     * <p>
     * <b>Note that at least one of the fallback pool elements MUST be a "true" terminator, i.e. have NO jigsaw pieces
     * except the one required for connection to its parent piece.</b> If all of your fallback pieces have more than one
     * jigsaw block, your structure's generation may get stuck in an infinite loop!
     * </p>
     */
    public final Optional<ResourceLocation> deadendPool;

    public final List<StructureModifier> modifiers;

    public YungJigsawSinglePoolElement(
            Either<ResourceLocation, StructureTemplate> resourceLocation,
            Holder<StructureProcessorList> processors,
            StructureTemplatePool.Projection projection,
            Optional<String> name,
            Optional<Integer> maxCount,
            Optional<Integer> minRequiredDepth,
            Optional<Integer> maxPossibleDepth,
            boolean isPriority,
            boolean ignoreBounds,
            StructureCondition condition,
//            Optional<EnhancedTerrainAdaptation> enhancedTerrainAdaptation,
            Optional<ResourceLocation> deadendPool,
            List<StructureModifier> modifiers
    ) {
        super(resourceLocation, processors, projection);
        this.maxCount = maxCount;
        this.name = name;
        this.minRequiredDepth = minRequiredDepth;
        this.maxPossibleDepth = maxPossibleDepth;
        this.isPriority = isPriority;
        this.ignoreBounds = ignoreBounds;
        this.condition = condition;
//        this.enhancedTerrainAdaptation = enhancedTerrainAdaptation;
        this.deadendPool = deadendPool;
        this.modifiers = modifiers;
    }

    public StructurePoolElementType<?> getType() {
        return StructurePoolElementTypeModule.YUNG_SINGLE_ELEMENT;
    }

    public boolean isPriorityPiece() {
        return isPriority;
    }

    public boolean ignoresBounds() {
        return this.ignoreBounds;
    }

    public boolean isAtValidDepth(int depth) {
        boolean isAtMinRequiredDepth = minRequiredDepth.isEmpty() || minRequiredDepth.get() <= depth;
        boolean isAtMaxAllowableDepth = maxPossibleDepth.isEmpty() || maxPossibleDepth.get() >= depth;
        return isAtMinRequiredDepth && isAtMaxAllowableDepth;
    }

    public boolean passesConditions(StructureContext ctx) {
        return this.condition.passes(ctx);
    }

    public String toString() {
        return String.format("YungJigsawSingle[%s][%s][%s][%s][%s][%s]",
                this.name,
                this.template,
                this.maxCount.isPresent() ? maxCount.get() : "N/A",
                this.minRequiredDepth.isPresent() ? "" + minRequiredDepth.get() : "N/A",
                this.maxPossibleDepth.isPresent() ? "" + maxPossibleDepth.get() : "N/A",
                this.isPriority);
    }

//    public EnhancedTerrainAdaptation getEnhancedTerrainAdaptation() {
//        return this.enhancedTerrainAdaptation.get();
//    }

//    public boolean hasEnhancedTerrainAdaptation() {
//        return this.enhancedTerrainAdaptation.isPresent();
//    }

    public ResourceLocation getDeadendPool() {
        return this.deadendPool.get();
    }

    public boolean hasDeadendPool() {
        return this.deadendPool.isPresent();
    }

    public boolean hasModifiers() {
        return this.modifiers.size() > 0;
    }
}
