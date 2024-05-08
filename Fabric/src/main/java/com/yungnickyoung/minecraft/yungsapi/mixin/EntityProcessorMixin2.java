package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * More mixins that allow for processing entities in Jigsaw structures.
 * This mixin is used in conjunction with StructureEntityInfoMixin.
 * This logic is separated into another mixin so that it can be applied with a higher priority,
 * allowing it to run after mixins from other mods like Porting Lib, thus ensuring compatibility.
 */
@Mixin(value = StructureTemplate.class, priority = 2000)
public class EntityProcessorMixin2 {
//    @ModifyExpressionValue(
//            method = "placeEntities",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/core/BlockPos;offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;"))
//    private BlockPos yungsapi_dontProcessBlockPosTwice(BlockPos original, @Local StructureTemplate.StructureEntityInfo info) {
//        if (((IStructureEntityInfoExtra) info).yungsapi$wasProcessed()) {
//            return info.blockPos;
//        } else {
//            return original;
//        }
//    }
//
//    @ModifyExpressionValue(
//            method = "placeEntities",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
//    private Vec3 yungsapi_dontProcessVecTwice(Vec3 original, @Local StructureTemplate.StructureEntityInfo info) {
//        if (((IStructureEntityInfoExtra) info).yungsapi$wasProcessed()) {
//            return info.pos;
//        } else {
//            return original;
//        }
//    }
}
