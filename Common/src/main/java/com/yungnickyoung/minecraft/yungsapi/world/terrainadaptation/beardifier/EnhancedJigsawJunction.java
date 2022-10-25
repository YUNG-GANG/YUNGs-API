package com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation.EnhancedTerrainAdaptation;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;

/**
 * Container for JigsawJunction + additional info used when processing {@link EnhancedTerrainAdaptation}s.
 */
public record EnhancedJigsawJunction(JigsawJunction jigsawJunction, EnhancedTerrainAdaptation enhancedTerrainAdaptation) {
}
