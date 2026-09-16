package com.yungnickyoung.minecraft.yungsapi.mixin.locate;

import com.yungnickyoung.minecraft.yungsapi.api.world.structure.locate.LocateReplacer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.locate.LocateReplacerImpl;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Overrides behavior of findNearestMapStructure for replaced vanilla structures
 * @see LocateReplacer
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @ModifyVariable(method = "findNearestMapStructure", at = @At("HEAD"), argsOnly = true)
    private HolderSet<Structure> yungsapi$replaceStructure(HolderSet<Structure> wantedStructures, ServerLevel level) {
        return LocateReplacerImpl.INSTANCE.getReplacement(level.registryAccess(), wantedStructures).orElse(wantedStructures);
    }
}
