package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.services.Services;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 11, ordinal = 0))
    private static int fastSpawn(int constant) {
        return Services.PLATFORM.isDevelopmentEnvironment() ? 0 : 11;
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441, ordinal = 0))
    private static int fastSpawn2(int constant) {
        return Services.PLATFORM.isDevelopmentEnvironment() ? 0 : 441;
    }
}
