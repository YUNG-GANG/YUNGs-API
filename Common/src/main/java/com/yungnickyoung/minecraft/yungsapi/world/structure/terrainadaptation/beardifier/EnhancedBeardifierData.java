package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.mixin.BeardifierMixin;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.levelgen.NoiseChunk;

import javax.annotation.Nullable;

/**
 * Utility interface for use with {@link BeardifierMixin}.
 */
public interface EnhancedBeardifierData {
    @Nullable ObjectListIterator<EnhancedBeardifierRigid> yungsapi_getEnhancedPieceIterator();
    void yungsapi_setEnhancedPieces(ObjectList<EnhancedBeardifierRigid> enhancedPieces);

    @Nullable ObjectListIterator<EnhancedJigsawJunction> yungsapi_getEnhancedJunctionIterator();
    void yungsapi_setEnhancedJunctions(ObjectList<EnhancedJigsawJunction> enhancedJunctions);

    @Nullable NoiseChunk yungsapi_getNoiseChunk();
    void yungsapi_setNoiseChunk(NoiseChunk noiseChunk);
}
