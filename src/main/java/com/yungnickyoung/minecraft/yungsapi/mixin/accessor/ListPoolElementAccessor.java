package com.yungnickyoung.minecraft.yungsapi.mixin.accessor;

import net.minecraft.world.level.levelgen.feature.structures.ListPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ListPoolElement.class)
public interface ListPoolElementAccessor {
    @Accessor
    List<StructurePoolElement> getElements();
}
