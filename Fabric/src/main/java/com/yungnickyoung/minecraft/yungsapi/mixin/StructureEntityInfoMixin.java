package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.processor.IStructureEntityInfoExtra;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StructureTemplate.StructureEntityInfo.class)
public class StructureEntityInfoMixin implements IStructureEntityInfoExtra {

    @Unique
    private boolean wasProcessedByYungsApi = false;

    @Override
    @Unique
    public boolean yungsapi$wasProcessed() {
        return wasProcessedByYungsApi;
    }

    @Override
    @Unique
    public void yungsapi$setProcessed(boolean bl) {
        wasProcessedByYungsApi = bl;
    }
}
