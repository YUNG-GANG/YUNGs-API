package com.yungnickyoung.minecraft.yungsapi.mixin.locate;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.yungnickyoung.minecraft.yungsapi.api.world.structure.locate.LocateReplacer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.locate.LocateReplacerImpl;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides behavior of /locate for replaced vanilla structures
 * @see LocateReplacer
 */
@Mixin(LocateCommand.class)
public abstract class LocateCommandMixin {
    @Inject(method = "locateStructure", at = @At(value = "HEAD"))
    private static void betterdeserttemples_overrideLocateVanillaPyramid(CommandSourceStack cmdSource,
                                                     ResourceOrTagKeyArgument.Result<Structure> result,
                                                     CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
        var replacement = LocateReplacerImpl.INSTANCE.getReplacement(result.unwrap());
        if (replacement.isPresent()) {
            throw LocateReplacerImpl.INSTANCE.makeCommandException(replacement.get());
        }
    }
}