package com.yungnickyoung.minecraft.yungsapi.mixin;

import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StructureTemplatePool.class)
public class StructurePoolMixin {
    /**
     * Increases the weight limit that mojang slapped on that was a workaround for https://bugs.mojang.com/browse/MC-203131
     * @author - TelepathicGrunt
     * @return - The higher weight that is a more reasonable limit.
     */
    @ModifyConstant(
        method = "method_28886",
        constant = @Constant(intValue = 150),
        remap = false,
        require = 0
    )
    private static int yungsapi_increaseWeightLimit(int constant) {
        return 5000;
    }
}
