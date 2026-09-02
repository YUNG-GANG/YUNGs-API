package com.yungnickyoung.minecraft.yungsapi.api.world.structure.processor;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * StructureEntityProcessorBuilders allow more-declaratively creating Structure Processors for processing Entities.
 * <p>
 *     This is used somewhat like {@link com.mojang.serialization.codecs.RecordCodecBuilder}. You can create a StructureEntityProcessorBuilder
 *     which extracts some things from the entity NBT and does something with them like this:
 * </p>
 * {@snippet :
 * processor(instance -> instance.group(
 *     extract(EntityEquipment.CODEC.fieldOf("equipment"))
 *            .map(eq -> eq.get(EquipmentSlot.HEAD).getItem())
 * ).apply(instance, item -> (_, _, _, globalEntityInfo, structurePlaceSettings) -> {
 *     if (item == Items.IRON_HELMET) {
 *         RandomSource random = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);
 *         return valueOutput -> valueOutput.putInt("my_value", random.nextInt(5));
 *     }
 * }));
 * }
 * <p>
 *     Above, we can see how a processor can:
 * </p>
 * <ul>
 *     <li>Extract a value from the existing NBT ({@code extract(EntityEquipment.CODEC.fieldOf("equipment"))})</li>
 *     <li>Manipulate that value ({@code .map(eq -> eq.get(EquipmentSlot.HEAD).getItem())})</li>
 *     <li>Make a decision based on that value ({@code if (item == Items.IRON_HELMET) })</li>
 *     <li>Write a new value to the entity NBT ({@code valueOutput.putInt("my_value", random.nextInt(5)) })</li>
 * </ul>
 * <p>
 *     A StructureEntityProcessorBuilder is essentially just a wrapper around a function from the NBT input to some optional
 *     output value. The StructureEntityProcessorBuilder can then be {@link #build(StructureEntityProcessorBuilder) built into an actual StructureProcessor}
 *     if that output value is of type {@link Processor}. The builders with other return types are used for in-between steps.
 * </p>
 * <p>
 *     Builders can also be {@link #guard(StructureEntityProcessorBuilder,StructureEntityProcessorBuilder) chained}.
 *     The second builder will not run if the first does not return a result. This can be used to make your processor
 *     only run on certain entity types, for example.
 * </p>
 * <p>
 *     There are some methods for creating commonly-used processor builders:
 * </p>
 * <ul>
 *     <li>{@link #createArmorStandProcessor(Function, Function, Function, Function)}</li>
 *     <li>{@link #createItemFrameProcessor(Function)}</li>
 * </ul>
 *
 * @param <F>   the result type of this builder
 */
public final class StructureEntityProcessorBuilder<F> implements App<StructureEntityProcessorBuilder.Mu, F> {
    private final Function<ValueInput, Optional<F>> function;

    /**
     * Create a new StructureEntityProcessorBuilder wrapping a function.
     * @param function  the function to wrap
     */
    public StructureEntityProcessorBuilder(final Function<ValueInput, Optional<F>> function) { this.function = function; }

    /**
     * A composable Entity Processor.
     */
    @FunctionalInterface
    public interface Processor {
        /**
         * Process the entity.
         * @param level                     access to the level
         * @param structurePiecePos         the position of this structure piece
         * @param localEntityInfo           the entity info in structure-space - it's rare to need this
         * @param globalEntityInfo          the entity info in real space
         * @param structurePlaceSettings    the structure placement settings
         * @return a function which writes new NBT data to the entity
         */
        Consumer<ValueOutput> process(
                LevelReader level,
                BlockPos structurePiecePos,
                StructureTemplate.StructureEntityInfo localEntityInfo,
                StructureTemplate.StructureEntityInfo globalEntityInfo,
                StructurePlaceSettings structurePlaceSettings);

        /**
         * Compose this processor with another
         * @param other the other processor
         * @return  a processor which runs this processor, then the other processor
         */
        default Processor andThen(Processor other) {
            return (level, pos, local, global, settings) -> valueOutput -> {
                this.process(level, pos, local, global, settings)
                        .andThen(other.process(level, pos, local, global, settings))
                        .accept(valueOutput);
            };
        }
    }

    /**
     * Create a StructureEntityProcessorBuilder which creates a {@link Processor}.
     * <p>This is analogous to {@link com.mojang.serialization.codecs.RecordCodecBuilder#create(Function)}.</p>
     * @param builder   the initialiser function for the builder
     * @return          the builder
     * @see #build(StructureEntityProcessorBuilder)
     */
    public static StructureEntityProcessorBuilder<Processor> processor(final Function<Instance, ? extends App<Mu, Processor>> builder) {
        return unbox(builder.apply(instance()));
    }

    /**
     * Create a generic StructureEntityProcessorBuilder.
     * @param builder   the initialiser function for the builder
     * @return          the builder
     * @see #processor(Function)
     */
    private static <F> StructureEntityProcessorBuilder<F> create(final Function<Instance, ? extends App<Mu, F>> builder) {
        return unbox(builder.apply(instance()));
    }

    /**
     * Create a StructureEntityProcessorBuilder which extracts some boolean value from the entity NBT.
     * @param function  the function which determines if the entity NBT matches
     * @return          the builder
     * @see #guard(StructureEntityProcessorBuilder, StructureEntityProcessorBuilder)
     */
    public static StructureEntityProcessorBuilder<Unit> onlyIf(Predicate<ValueInput> function) {
        return new StructureEntityProcessorBuilder<>(vi -> function.test(vi) ? Optional.of(Unit.INSTANCE) : Optional.empty());
    }

    /**
     * Create a predicate on entity NBT which determines whether the entity is a certain type.
     * @param key   the key of the entity type
     * @return      a predicate
     * @see #onlyIf(Predicate)
     * @see #is(EntityType)
     */
    public static Predicate<ValueInput> is(ResourceKey<EntityType<?>> key) {
        return input -> input.read("id", ResourceKey.codec(Registries.ENTITY_TYPE)).filter(k -> k == key).isPresent();
    }

    /**
     * Create a predicate on entity NBT which determines whether the entity is a certain type.
     * @param type  the entity type
     * @return      a predicate
     * @see #onlyIf(Predicate)
     * @see #is(ResourceKey)
     */
    @SuppressWarnings("deprecation")
    public static Predicate<ValueInput> is(EntityType<?> type) {
        return is(type.builtInRegistryHolder().key());
    }

    /**
     * Create a StructureEntityProcessorBuilder from two others. The second processor will only be run if the first
     * processor returns a result.
     * @param first     the first builder, which always runs
     * @param second    the second builder, which only runs if the first does
     * @return          a combined builder
     * @param <F>       the return type of the second StructureEntityProcessorBuilder
     */
    public static <F> StructureEntityProcessorBuilder<F> guard(final StructureEntityProcessorBuilder<?> first, StructureEntityProcessorBuilder<F> second) {
        return create(instance -> instance.group(first).apply(instance, _ -> get(second)))
                .flatMapWithInput((vi, f) -> f.apply(vi));
    }

    /**
     * Create a StructureEntityProcessorBuilder from some number of others. Each successive builder will only be
     * run if the ones before it return results.
     * @param builders  the builders
     * @return          a combined builder
     */
    @SafeVarargs
    public static StructureEntityProcessorBuilder<Processor> seq(final StructureEntityProcessorBuilder<Processor>... builders) {
        StructureEntityProcessorBuilder<Processor> last = builders[0];
        for (int i = 1; i < builders.length; i++) {
            StructureEntityProcessorBuilder<Processor> first = last;
            StructureEntityProcessorBuilder<Processor> second = builders[i];
            last = create(instance -> instance.group(first).apply(instance, p1 -> get(second.map(p2 -> p1.andThen(p2)))))
                    .flatMapWithInput((vi, f) -> f.apply(vi));
        }

        return last;
    }

    /**
     * Create a StructureEntityProcessorBuilder which extracts a value from the entity NBT.
     * @param decoder the map decoder or map codec which extracts the value
     * @return      the builder
     * @param <F>   the type of the value to be extracted
     * @see #extract(String, Decoder)
     */
    public static <F> StructureEntityProcessorBuilder<F> extract(final MapDecoder<F> decoder) {
        /*
         For some reason ValueInput doesn't work with plain MapDecoders so we're doing this lol.
         However we know it will never be used to encode so all okay.
        */
        MapCodec<F> codec = decoder instanceof MapCodec<F> c ? c : MapCodec.of(new MapEncoder<>() {
            @Override
            public <T> RecordBuilder<T> encode(final F input,
                    final DynamicOps<T> ops,
                    final RecordBuilder<T> prefix) {
                return prefix.withErrorsFrom(DataResult.error(() -> "Tried to encode with a decoder-only codec (from StructureEntityProcessorBuilder#extract)"));
            }

            @Override public <T> KeyCompressor<T> compressor(final DynamicOps<T> ops) {
                return decoder.compressor(ops);
            }

            @Override public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return decoder.keys(ops);
            }
        }, decoder);

        return new StructureEntityProcessorBuilder<>(i -> i.read(codec));
    }

    /**
     * Create a StructureEntityProcessorBuilder which extracts a value from the entity NBT.
     * @param key     the key to extract the value from
     * @param decoder the decoder or codec which extracts the value
     * @return      the builder
     * @param <F>   the type of the value to be extracted
     * @see #extract(String, Decoder)
     */
    public static <F> StructureEntityProcessorBuilder<F> extract(final String key, final Decoder<F> decoder) {
        /*
         For some reason ValueInput doesn't work with plain MapDecoders so we're doing this lol.
         However we know it will never be used to encode so all okay.
        */
        Codec<F> codec = decoder instanceof Codec<F> c ? c : Codec.of(new Encoder<>() {
            @Override public <T> DataResult<T> encode(final F input, final DynamicOps<T> ops, final T prefix) {
                return DataResult.error(() -> "Tried to encode with a decoder-only codec (from StructureEntityProcessorBuilder#extract)");
            }
        }, decoder);

        return new StructureEntityProcessorBuilder<>(i -> i.read(key, codec));
    }

    /**
     * Create a StructureEntityProcessorBuilder which returns a constant value
     * @param f     the constant value
     * @return      the builder
     * @param <F>   the type of the constant value
     */
    public static <F> StructureEntityProcessorBuilder<F> of(F f) {
        return new StructureEntityProcessorBuilder<>(_ -> Optional.of(f));
    }

    /**
     * Map this StructureEntityProcessorBuilder.
     * @param function  the mapping function
     * @return          the mapped builder
     * @param <R>       the return type of the new builder
     */
    public <R> StructureEntityProcessorBuilder<R> map(Function<F, R> function) {
        return new StructureEntityProcessorBuilder<>(this.function.andThen(o -> o.map(function)));
    }

    /**
     * Flatmap this StructureEntityProcessorBuilder.
     * @param function  the flatmapping function
     * @return          the flatmapped builder
     * @param <R>       the return type of the new builder
     */
    public <R> StructureEntityProcessorBuilder<R> flatMap(Function<F, Optional<R>> function) {
        return new StructureEntityProcessorBuilder<>(this.function.andThen(o -> o.flatMap(function)));
    }

    /**
     * Flatmap this StructureEntityProcessorBuilder with access to the entity NBT.
     * @param function  the flatmapping function
     * @return          the flatmapped builder
     * @param <R>       the return type of the new builder
     */
    public <R> StructureEntityProcessorBuilder<R> flatMapWithInput(BiFunction<ValueInput, F, Optional<R>> function) {
        return new StructureEntityProcessorBuilder<>(vi -> this.function.apply(vi).flatMap(f -> function.apply(vi, f)));
    }

    /**
     * Filter this StructureEntityProcessorBuilder.
     * @param function  the filtering function
     * @return          the filtered builder
     */
    public StructureEntityProcessorBuilder<F> filter(Predicate<F> function) {
        return new StructureEntityProcessorBuilder<>(this.function.andThen(o -> o.filter(function)));
    }

    /**
     * Create a StructureEntityProcessorBuilder which gives Armor Stands randomized armor based on their helmet item.
     * @param bootsMap      the mapping from helmet item to random boots item
     * @param leggingsMap   the mapping from helmet item to random leggings item
     * @param chestMap      the mapping from helmet item to random chestplate item
     * @param helmetMap     the mapping from helmet item to random helmet item
     * @return  the builder
     */
    public static StructureEntityProcessorBuilder<Processor> createArmorStandProcessor(
            Function<Item, Function<RandomSource, Item>> bootsMap,
            Function<Item, Function<RandomSource, Item>> leggingsMap,
            Function<Item, Function<RandomSource, Item>> chestMap,
            Function<Item, Function<RandomSource, Item>> helmetMap) {
        return seq(
                guard(
                        onlyIf(is(EntityType.ARMOR_STAND)),
                        processor(instance -> instance.group(
                                extract(EntityEquipment.CODEC.fieldOf("equipment"))
                                        .map(eq -> eq.get(EquipmentSlot.HEAD).getItem())
                        ).apply(instance, key -> (_, _, _, globalEntityInfo, structurePlaceSettings) -> {
                            RandomSource random = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);
                            var newEquipment = new EntityEquipment();
                            newEquipment.set(EquipmentSlot.FEET,  bootsMap.apply(key).apply(random).getDefaultInstance());
                            newEquipment.set(EquipmentSlot.LEGS,  leggingsMap.apply(key).apply(random).getDefaultInstance());
                            newEquipment.set(EquipmentSlot.CHEST, chestMap.apply(key).apply(random).getDefaultInstance());
                            newEquipment.set(EquipmentSlot.HEAD,  helmetMap.apply(key).apply(random).getDefaultInstance());
                            return valueOutput -> {
                                valueOutput.store("equipment", EntityEquipment.CODEC, newEquipment);
                            };
                        }))));
    }

    /**
     * Create a StructureEntityProcessorBuilder which gives Item Frames randomized items based on their original item,
     * fixes their NBT block position (which fixes logspam), and gives them random rotation.
     * @param map   the mapping from original item to random new item
     * @return  the builder
     */
    public static StructureEntityProcessorBuilder<Processor> createItemFrameProcessor(Function<Item, Function<RandomSource, Item>> map) {
        return seq(
                guard(
                        onlyIf(is(EntityType.ITEM_FRAME)),
                        processor(instance -> instance.group(
                                extract(ItemStackTemplate.CODEC.fieldOf("Item"))
                                        .map(ist -> map.apply(ist.item().value()))
                                        .filter(i -> i != Items.AIR)
                        ).apply(instance, lootFunc -> (_, _, _, globalEntityInfo, structurePlaceSettings) -> {
                            RandomSource random = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);
                            Item item = lootFunc.apply(random);
                            if (item == Items.AIR) {
                                return _ -> {};
                            }
                            return valueOutput -> {
                                valueOutput.child("Item").store("id", Identifier.CODEC, item.builtInRegistryHolder().key().identifier());
                            };
                        }))),
                createBlockPosFixProcessor(),
                createRandomItemStackRotationProcessor());
    }

    /**
     * Create a StructureEntityProcessorBuilder which fixes entities' NBT block position. This fixes logspam for certain
     * entities, such as Item Frames.
     * @return  the builder
     */
    public static StructureEntityProcessorBuilder<Processor> createBlockPosFixProcessor() {
        return of((_, _, _, globalEntityInfo, _) -> valueOutput ->
                valueOutput.store("block_pos", BlockPos.CODEC, globalEntityInfo.blockPos));
    }

    /**
     * Create a StructureEntityProcessorBuilder which applies random rotation to Item Frames.
     * @return  the builder
     */
    public static StructureEntityProcessorBuilder<Processor> createRandomItemStackRotationProcessor() {
        return of((_, _, _, globalEntityInfo, structurePlaceSettings) -> {
            RandomSource random = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);
            int randomRotation = random.nextInt(8);
            return valueOutput -> valueOutput.putByte("ItemRotation", (byte) randomRotation);
        });
    }

    /**
     * Build the StructureProcessorType from the StructureEntityProcessorBuilder
     * @param builder   the builder
     * @return  the StructureProcessorType
     */
    public static StructureProcessorType<StructureProcessor> build(final StructureEntityProcessorBuilder<Processor> builder) {
        return Services.STRUCTURE_PROCESSOR_HELPER.createStructureProcessorType(builder.function);
    }

    /**
     * Unbox to a StructureEntityProcessorBuilder
     * @param box   the boxed StructureEntityProcessorBuilder
     * @return      the unboxed StructureEntityProcessorBuilder
     * @param <F>   the builder's return type
     */
    private static <F> StructureEntityProcessorBuilder<F> unbox(final App<Mu, F> box) {
        return ((StructureEntityProcessorBuilder<F>) box);
    }

    /**
     * Get the function contained within a StructureEntityProcessorBuilder.
     * @param app   the StructureEntityProcessorBuilder
     * @return      the function
     * @param <F>   the return type of the function
     */
    private static <F> Function<ValueInput, Optional<F>> get(App<Mu, F> app) {
        return unbox(app).function;
    }

    /**
     * {@return a new Instance}
     */
    private static Instance instance() {
        return new Instance();
    }

    /**
     * Witness type
     */
    public static final class Mu implements K1 { }

    /**
     * The Instance for the builder's Applicative Functor.
     */
    public static final class Instance implements Applicative<Mu, Instance.Mu> {
        @Override public <A> App<StructureEntityProcessorBuilder.Mu, A> point(final A a) {
            return of(a);
        }

        @Override
        public <A, R> Function<App<StructureEntityProcessorBuilder.Mu, A>, App<StructureEntityProcessorBuilder.Mu, R>> lift1(
                final App<StructureEntityProcessorBuilder.Mu, Function<A, R>> function) {
            return a -> {
                final var aProcessor = get(a);
                final var fProcessor = get(function);
                return new StructureEntityProcessorBuilder<>(valueInput ->
                        aProcessor.apply(valueInput)
                                .flatMap(aa -> fProcessor.apply(valueInput)
                                        .map(ff -> ff.apply(aa))));
            };
        }

        @Override
        public <T, R> App<StructureEntityProcessorBuilder.Mu, R> map(final Function<? super T, ? extends R> func,
                final App<StructureEntityProcessorBuilder.Mu, T> ts) {
            var processor = get(ts);
            return new StructureEntityProcessorBuilder<>(valueInput -> processor.apply(valueInput).map(func));
        }

        /**
         * Witness type
         */
        private static final class Mu implements Applicative.Mu { }
    }
}
