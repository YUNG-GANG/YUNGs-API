package com.yungnickyoung.minecraft.yungsapi.api.world.structure.locate;

import com.mojang.datafixers.util.Either;
import com.yungnickyoung.minecraft.yungsapi.world.structure.locate.LocateReplacerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.function.Supplier;

/**
 * Allows replacing vanilla structures / structure tags in the {@code /locate} command with a modded version.
 * <p>
 *     When the user tries to run {@code /locate} on a replaced structure, they will be told to use the replacement
 *     structure / tag key.
 * </p>
 * <p>
 *     If a mod runs {@link net.minecraft.server.level.ServerLevel#findNearestMapStructure(TagKey, BlockPos, int, boolean)}
 *     or {@link net.minecraft.world.level.chunk.ChunkGenerator#findNearestMapStructure(ServerLevel, HolderSet, BlockPos, int, boolean)},
 *     the searched-for structure will be swapped out.
 * </p>
 */
public final class LocateReplacer {
    public static void register(ResourceKey<Structure> vanillaStructure, ResourceKey<Structure> replacementStructure, Supplier<Boolean> isEnabled) {
        LocateReplacerImpl.INSTANCE.register(Either.left(vanillaStructure), Either.left(replacementStructure), isEnabled);
    }
    public static void register(ResourceKey<Structure> vanillaStructure, TagKey<Structure> replacementStructure, Supplier<Boolean> isEnabled) {
        LocateReplacerImpl.INSTANCE.register(Either.left(vanillaStructure), Either.right(replacementStructure), isEnabled);
    }
    public static void register(TagKey<Structure> vanillaStructure, ResourceKey<Structure> replacementStructure, Supplier<Boolean> isEnabled) {
        LocateReplacerImpl.INSTANCE.register(Either.right(vanillaStructure), Either.left(replacementStructure), isEnabled);
    }
    public static void register(TagKey<Structure> vanillaStructure, TagKey<Structure> replacementStructure, Supplier<Boolean> isEnabled) {
        LocateReplacerImpl.INSTANCE.register(Either.right(vanillaStructure), Either.right(replacementStructure), isEnabled);
    }
}
