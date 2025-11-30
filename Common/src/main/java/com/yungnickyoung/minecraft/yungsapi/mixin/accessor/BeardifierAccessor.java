package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Beardifier.class)
public interface BeardifierAccessor {
    @Accessor("pieces")
    List<Beardifier.Rigid> getPieces();

    @Accessor("junctions")
    List<JigsawJunction> getJunctions();

    @Accessor("affectedBox")
    BoundingBox getAffectedBox();
}