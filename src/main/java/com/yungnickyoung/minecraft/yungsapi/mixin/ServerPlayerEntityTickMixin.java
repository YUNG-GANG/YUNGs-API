package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.mojang.authlib.GameProfile;
import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import com.yungnickyoung.minecraft.yungsapi.init.YAModCriteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityTickMixin extends PlayerEntity {
    public ServerPlayerEntityTickMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectMethod(CallbackInfo info) {
        if (this.age % 20 == 0) {
            YAModCriteria.SAFE_STRUCTURE_LOCATION.trigger(this);
        }
    }
}
