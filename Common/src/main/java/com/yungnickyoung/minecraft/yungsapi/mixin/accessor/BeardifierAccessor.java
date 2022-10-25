package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Beardifier.class)
public interface BeardifierAccessor {
    @Invoker
    static double callGetBeardContribution(int $$0, int $$1, int $$2, int $$3) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static float[] getBEARD_KERNEL() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    ObjectListIterator<Beardifier.Rigid> getPieceIterator();

    @Accessor
    ObjectListIterator<JigsawJunction> getJunctionIterator();
}
