package com.yungnickyoung.minecraft.yungsapi.world.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.mixin.BeardifierMixin;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;

/**
 * Utility interface for use with {@link BeardifierMixin}.
 */
public interface EnhancedBeardifierData {
    ObjectListIterator<EnhancedBeardifierRigid> getEnhancedRigidIterator();
    void setEnhancedRigidIterator(ObjectListIterator<EnhancedBeardifierRigid> enhancedRigidIterator);
    ObjectListIterator<EnhancedJigsawJunction> getEnhancedJunctionIterator();
    void setEnhancedJunctionIterator(ObjectListIterator<EnhancedJigsawJunction> enhancedJunctionIterator);
}
