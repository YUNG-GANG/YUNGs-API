package com.yungnickyoung.minecraft.yungsapi.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes a crash that occurs when a jukebox is overwritten during worldgen.
 */
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
@Mixin(JukeboxBlockEntity.class)
public abstract class FixJukeboxCrashMixin extends BlockEntity {
    public FixJukeboxCrashMixin(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    @Inject(method = "setTheItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;registryAccess()Lnet/minecraft/core/RegistryAccess;"),
            cancellable = true,
            require = 0)
    public void yungsapi_checkIfLevelNull(ItemStack itemStack, CallbackInfo ci) {
        if (this.level == null) {
            ci.cancel();
        }
    }

    // NeoForge has split the setTheItem method into itemChanged and setTheItem. In NeoForge, we need to inject into itemChanged.
    @Inject(method = "itemChanged",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;registryAccess()Lnet/minecraft/core/RegistryAccess;"),
            cancellable = true,
            require = 0)
    public void yungsapi_checkIfLevelNullForNeo(CallbackInfo ci) {
        if (this.level == null) {
            ci.cancel();
        }
    }
}
