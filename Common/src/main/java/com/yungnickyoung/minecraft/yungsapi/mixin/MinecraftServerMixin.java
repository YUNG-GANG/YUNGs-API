package com.yungnickyoung.minecraft.yungsapi.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import com.yungnickyoung.minecraft.yungsapi.YungsApiMixinPlugin;

/**
 * Skips initial chunk generation when creating a new world. Used in development environments only.
 * Uses a custom mixin plugin via {@link YungsApiMixinPlugin} to only activate in development environments.
 */
@Mixin(value = MinecraftServer.class, priority = 500)
public class MinecraftServerMixin {
    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 11, ordinal = 0))
    private static int fastSpawn(int constant) {
        return 0;
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441, ordinal = 0))
    private static int fastSpawn2(int constant) {
        return 0;
    }
}
