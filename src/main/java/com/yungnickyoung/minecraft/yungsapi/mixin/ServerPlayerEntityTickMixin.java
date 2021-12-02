package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.mojang.authlib.GameProfile;
import com.yungnickyoung.minecraft.yungsapi.init.YAModCriteria;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityTickMixin extends Player {
    public ServerPlayerEntityTickMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectMethod(CallbackInfo info) {
        if (this.tickCount % 20 == 0) {
            YAModCriteria.SAFE_STRUCTURE_LOCATION.trigger(this);
        }
    }
}