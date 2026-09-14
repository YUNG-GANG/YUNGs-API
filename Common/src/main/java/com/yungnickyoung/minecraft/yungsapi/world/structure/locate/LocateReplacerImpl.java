package com.yungnickyoung.minecraft.yungsapi.world.structure.locate;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LocateReplacerImpl {
    public static final LocateReplacerImpl INSTANCE = new LocateReplacerImpl();
    private static final DynamicCommandExceptionType REPLACED_COMMAND_EXCEPTION = new DynamicCommandExceptionType(o ->
            Component.translatable("command.yungsapi.locate.replaced",
                    Component.literal("/locate structure "+o).withStyle(s -> s.withUnderlined(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "locate structure "+o)))));
    private static final DynamicCommandExceptionType REMOVED_COMMAND_EXCEPTION = new DynamicCommandExceptionType(o -> o instanceof Message message ? message : Component.empty());
    private final Map<ResourceKey<Structure>, Replacement> keyReplacements = new HashMap<>();
    private final Map<ResourceKey<Structure>, Removal> keyRemovals = new HashMap<>();
    private final Map<TagKey<Structure>, Replacement> tagReplacements = new HashMap<>();
    private final Map<TagKey<Structure>, Removal> tagRemovals = new HashMap<>();

    public void registerReplacement(final Either<ResourceKey<Structure>, TagKey<Structure>> vanillaKey,
                                    final Either<ResourceKey<Structure>, TagKey<Structure>> replacementKey,
                                    final Supplier<Boolean> isEnabled) {
        var replacement = new Replacement(replacementKey, isEnabled);
        vanillaKey.ifLeft(rk -> this.keyReplacements.put(rk, replacement));
        vanillaKey.ifRight(tk -> this.tagReplacements.put(tk, replacement));
    }

    public void registerRemoval(final Either<ResourceKey<Structure>, TagKey<Structure>> vanillaKey,
                         final Component message,
                         final Supplier<Boolean> isEnabled) {
        var removal = new Removal(message, isEnabled);
        vanillaKey.ifLeft(rk -> this.keyRemovals.put(rk, removal));
        vanillaKey.ifRight(tk -> this.tagRemovals.put(tk, removal));
    }

    public Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> getReplacement(ResourceKey<Structure> vanilla) {
        return getReplacement(vanilla, this.keyReplacements);
    }

    public Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> getReplacement(TagKey<Structure> vanilla) {
        return getReplacement(vanilla, this.tagReplacements);
    }

    public Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> getReplacement(Either<ResourceKey<Structure>, TagKey<Structure>> vanilla) {
        return vanilla.map(this::getReplacement, this::getReplacement);
    }

    public Optional<Component> getRemoval(ResourceKey<Structure> vanilla) {
        return getRemoval(vanilla, this.keyRemovals);
    }

    public Optional<Component> getRemoval(TagKey<Structure> vanilla) {
        return getRemoval(vanilla, this.tagRemovals);
    }

    public Optional<Component> getRemoval(Either<ResourceKey<Structure>, TagKey<Structure>> vanilla) {
        return vanilla.map(this::getRemoval, this::getRemoval);
    }

    public Optional<HolderSet<Structure>> getReplacement(HolderLookup.Provider registries, HolderSet<Structure> vanilla) {
        if (vanilla.size() == 0) {
            return Optional.empty();
        }

        if (vanilla instanceof HolderSet.Named<Structure> named) {
            return this.getReplacement(named.key()).map(key -> toHolderSet(registries, key));
        } else if (vanilla.size() == 1) {
            return vanilla.get(0).unwrapKey().flatMap(this::getReplacement).map(key -> toHolderSet(registries, key));
        }

        return Optional.empty();
    }

    public Optional<CommandSyntaxException> getCommandException(final Either<ResourceKey<Structure>, TagKey<Structure>> vanilla) {
        return this.getReplacement(vanilla)
                .map(LocateReplacerImpl::getCommandExceptionForReplacement)
                .or(() -> this.getRemoval(vanilla).map(REMOVED_COMMAND_EXCEPTION::create));
    }

    public Stream<ResourceKey<Structure>> getReplacedOrRemovedStructures() {
        return Stream.concat(
                        this.keyReplacements.entrySet().stream()
                                .filter(e -> e.getValue().isEnabled().get()),
                        this.keyRemovals.entrySet().stream()
                                .filter(e -> e.getValue().isEnabled().get()))
                .map(Map.Entry::getKey);
    }

    private static HolderSet<Structure> toHolderSet(final HolderLookup.Provider registries, Either<ResourceKey<Structure>, TagKey<Structure>> key) {
        return key.map(rk -> registries.lookup(Registries.STRUCTURE).flatMap(rl -> rl.get(rk).map(HolderSet::direct)).orElseGet(HolderSet::direct),
                tk -> registries.lookup(Registries.STRUCTURE).flatMap(rl -> rl.get(tk).map(hs -> (HolderSet<Structure>) hs)).orElseGet(HolderSet::direct));
    }

    private static <T> Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> getReplacement(T vanilla, Map<T, Replacement> map) {
        var replacement = map.get(vanilla);
        if (replacement == null) {
            return Optional.empty();
        } else {
            return replacement.isEnabled().get() ? Optional.of(replacement.key()) : Optional.empty();
        }
    }

    private static <T> Optional<Component> getRemoval(T vanilla, Map<T, Removal> map) {
        var replacement = map.get(vanilla);
        if (replacement == null) {
            return Optional.empty();
        } else {
            return replacement.isEnabled().get() ? Optional.of(replacement.commandMessage()) : Optional.empty();
        }
    }

    private static CommandSyntaxException getCommandExceptionForReplacement(final Either<ResourceKey<Structure>, TagKey<Structure>> replacement) {
        return REPLACED_COMMAND_EXCEPTION.create(replacement.map(rk -> rk.location().toString(), tk -> "#"+tk.location()));
    }

    private record Replacement(Either<ResourceKey<Structure>, TagKey<Structure>> key, Supplier<Boolean> isEnabled) {}
    private record Removal(Component commandMessage, Supplier<Boolean> isEnabled) {}
}
