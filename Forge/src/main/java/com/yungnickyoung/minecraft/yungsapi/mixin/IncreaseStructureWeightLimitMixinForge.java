package com.yungnickyoung.minecraft.yungsapi.mixin;

import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StructureTemplatePool.class)
public class IncreaseStructureWeightLimitMixinForge {
    /**
     * Increases the weight limit that mojang slapped on that was a workaround for <a href="https://bugs.mojang.com/browse/MC-203131">...</a>
     * @author - TelepathicGrunt
     * @return - The higher weight that is a more reasonable limit.
     */
    @ModifyConstant(
            method = "lambda$static$1(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;",
            constant = @Constant(intValue = 150),
            require = 0
    )
    private static int yungsapi_increaseWeightLimit(int constant) {
        return 5000;
    }
}
