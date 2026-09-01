package com.yungnickyoung.minecraft.yungsapi.world.structure.locate;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class LocateReplacerImpl {
    public static final LocateReplacerImpl INSTANCE = new LocateReplacerImpl();
    private static final DynamicCommandExceptionType REPLACED_COMMAND_EXCEPTION = new DynamicCommandExceptionType(o ->
            Component.translatable("command.yungsapi.locate.replaced", o));
    private final Map<ResourceKey<Structure>, Replacement> keyReplacements = new HashMap<>();
    private final Map<TagKey<Structure>, Replacement> tagReplacements = new HashMap<>();

    public void register(final Either<ResourceKey<Structure>, TagKey<Structure>> vanillaKey,
            final Either<ResourceKey<Structure>, TagKey<Structure>> replacementKey,
            final Supplier<Boolean> isEnabled) {
        var replacement = new Replacement(replacementKey, isEnabled);
        vanillaKey.ifLeft(rk -> this.keyReplacements.put(rk, replacement));
        vanillaKey.ifRight(tk -> this.tagReplacements.put(tk, replacement));
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

    public Optional<HolderSet<Structure>> getReplacement(HolderLookup.Provider registries, HolderSet<Structure> vanilla) {
        if (!vanilla.isBound()) {
            return Optional.empty();
        }

        if (vanilla instanceof HolderSet.Named<Structure> named) {
            return this.getReplacement(named.key()).map(key -> toHolderSet(registries, key));
        } else if (vanilla.size() == 1) {
            return vanilla.get(0).unwrapKey().flatMap(this::getReplacement).map(key -> toHolderSet(registries, key));
        }

        return Optional.empty();
    }

    public CommandSyntaxException makeCommandException(final Either<ResourceKey<Structure>, TagKey<Structure>> replacement) {
        return REPLACED_COMMAND_EXCEPTION.create(replacement.map(rk -> rk.identifier().toString(), tk -> "#"+tk.location()));
    }

    private static HolderSet<Structure> toHolderSet(final HolderLookup.Provider registries, Either<ResourceKey<Structure>, TagKey<Structure>> key) {
        return key.map(rk -> registries.get(rk).map(HolderSet::direct).orElseGet(HolderSet::direct),
                tk -> registries.get(tk).map(hs -> (HolderSet<Structure>) hs).orElseGet(HolderSet::direct));
    }

    private static <T> Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> getReplacement(T vanilla, Map<T, Replacement> map) {
        var replacement = map.get(vanilla);
        if (replacement == null) {
            return Optional.empty();
        } else {
            return replacement.isEnabled().get() ? Optional.of(replacement.key()) : Optional.empty();
        }
    }

    private record Replacement(Either<ResourceKey<Structure>, TagKey<Structure>> key, Supplier<Boolean> isEnabled) {}
}
