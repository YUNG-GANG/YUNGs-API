package com.yungnickyoung.minecraft.yungsapi.mixin.locate;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Either;
import com.yungnickyoung.minecraft.yungsapi.api.world.structure.locate.LocateReplacer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.locate.LocateReplacerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Overrides behavior of findNearestMapStructure for replaced vanilla structures
 * @see LocateReplacer
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "findNearestMapStructure", at = @At("HEAD"))
    private void yungsapi$replaceStructure(final TagKey<Structure> structureTag,
            final BlockPos origin,
            final int maxSearchRadius,
            final boolean createReference,
            final CallbackInfoReturnable<BlockPos> cir,
            final @Share("replacement") LocalRef<Optional<Either<ResourceKey<Structure>, TagKey<Structure>>>> replacement) {
        replacement.set(LocateReplacerImpl.INSTANCE.getReplacement(structureTag));
    }

    // because the holderset this method uses is explicitly a tag-based one, we can't use it for our replacement, so
    // we have to put the replacement in another variable and replace usages
    @WrapOperation(method = "findNearestMapStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;get(Lnet/minecraft/tags/TagKey;)Ljava/util/Optional;"))
    private Optional<HolderSet.Named<Structure>> yungsapi$replaceHolderSetForStructure(
            final Registry<Structure> instance,
            final TagKey<Structure> tagKey,
            final Operation<Optional<HolderSet.Named<Structure>>> original,
            final @Share("replacement") LocalRef<Optional<Either<ResourceKey<Structure>, TagKey<Structure>>>> replacement,
            final @Share("replacementSet") LocalRef<Optional<HolderSet<Structure>>> replacementSet) {
        replacementSet.set(Optional.empty());
        if (replacement.get().isPresent() && replacement.get().get().right().isPresent()) {
            var tk = replacement.get().get().right().get();
            return original.call(instance, tk);
        } else {
            replacement.get().flatMap(e -> e.left())
                    .flatMap(instance::get)
                    .ifPresent(structure -> replacementSet.set(Optional.of(HolderSet.direct(structure))));
            return original.call(instance, tagKey);
        }
    }

    @WrapOperation(method = "findNearestMapStructure", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isEmpty()Z"))
    private boolean yungsapi$checkIfReplacementIsEmpty(
            final Optional<HolderSet.Named<Structure>> instance,
            final Operation<Boolean> original,
            final @Share("replacementSet") LocalRef<Optional<HolderSet<Structure>>> replacementSet) {
        return original.call(instance) && replacementSet.get().isEmpty();
    }

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    @WrapOperation(method = "findNearestMapStructure", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    private <T> T yungsapi$useReplacementSet(
            final Optional<HolderSet.Named<Structure>> instance,
            final Operation<Boolean> original,
            final @Share("replacementSet") LocalRef<Optional<HolderSet<Structure>>> replacementSet) {
        return (T) (instance.isPresent() ? original.call(instance) : replacementSet.get().get());
    }
}
