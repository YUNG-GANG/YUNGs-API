package com.yungnickyoung.minecraft.yungsapi.mixin.locate;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.api.world.structure.locate.LocateReplacer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.locate.LocateReplacerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides behavior of findNearestMapStructure for replaced vanilla structures
 * @see LocateReplacer
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(method = "findNearestMapStructure", at = @At("HEAD"))
    private void yungsapi$replaceStructure(final ServerLevel level,
            final HolderSet<Structure> wantedStructures,
            final BlockPos pos,
            final int maxSearchRadius,
            final boolean createReference,
            final CallbackInfoReturnable<Pair<BlockPos, Holder<Structure>>> cir,
            final @Local(argsOnly = true) LocalRef<HolderSet<Structure>> replacement) {
        LocateReplacerImpl.INSTANCE.getReplacement(level.registryAccess(), wantedStructures).ifPresent(replacement::set);
    }
}
