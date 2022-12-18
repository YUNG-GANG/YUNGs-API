package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.mojang.authlib.GameProfile;
import com.yungnickyoung.minecraft.yungsapi.module.CriteriaModule;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityTickMixin extends Player {

    public ServerPlayerEntityTickMixin(Level $$0, BlockPos $$1, float $$2, GameProfile $$3) {
        super($$0, $$1, $$2, $$3);
    }

    /**
     * Triggers custom criteria.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void injectMethod(CallbackInfo info) {
        if (this.tickCount % 20 == 0) {
            CriteriaModule.SAFE_STRUCTURE_LOCATION.trigger(this);
        }
    }
}