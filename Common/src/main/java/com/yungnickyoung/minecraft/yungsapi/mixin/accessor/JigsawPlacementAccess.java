package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(JigsawPlacement.class)
public interface JigsawPlacementAccess {
    @Invoker("getRandomNamedJigsaw")
    static Optional<BlockPos> yungs_getRandomNamedJigsaw(StructurePoolElement $$0, ResourceLocation $$1, BlockPos $$2, Rotation $$3, StructureTemplateManager $$4, WorldgenRandom $$5) {
        throw new Error("Mixin did not apply!");
    }
}
