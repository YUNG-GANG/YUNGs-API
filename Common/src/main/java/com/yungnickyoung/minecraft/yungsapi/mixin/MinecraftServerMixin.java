package com.yungnickyoung.minecraft.yungsapi.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Skips initial chunk generation when creating a new world.
 * Only runs in development environment, with the help of {@link com.yungnickyoung.minecraft.yungsapi.YungsApiMixinPlugin}
 */
@Mixin(value = MinecraftServer.class, priority = 500)
public class MinecraftServerMixin {
    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 11, ordinal = 0))
    private static int yungsapi_fastSpawn(int constant) {
        return 0;
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441, ordinal = 0))
    private static int yungsapi_fastSpawn2(int constant) {
        return 0;
    }
}
