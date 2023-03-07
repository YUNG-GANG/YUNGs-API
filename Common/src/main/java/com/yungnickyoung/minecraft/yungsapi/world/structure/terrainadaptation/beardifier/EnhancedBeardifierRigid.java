package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.EnhancedTerrainAdaptation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Equivalent to vanilla's Beardifier.Rigid (1.19+ only), but with an {@link EnhancedTerrainAdaptation} instead of
 * vanilla's TerrainAdjustment (1.19+ only).
 */
public record EnhancedBeardifierRigid(BoundingBox pieceBoundingBox, EnhancedTerrainAdaptation pieceTerrainAdaptation, int pieceGroundLevelDelta) {
}
