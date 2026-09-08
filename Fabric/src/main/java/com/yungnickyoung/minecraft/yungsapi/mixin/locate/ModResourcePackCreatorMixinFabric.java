package com.yungnickyoung.minecraft.yungsapi.mixin.locate;

import com.yungnickyoung.minecraft.yungsapi.world.structure.locate.LocateReplacerDataPackResources;
import net.fabricmc.fabric.impl.resource.pack.ModResourcePackCreator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ModResourcePackCreator.class)
public class ModResourcePackCreatorMixinFabric {
    @Shadow @Final
    private PackType type;

    @Unique
    private LocateReplacerDataPackResources.@Nullable Source runtimeDataPackSource;

    @Inject(method = "<init>(Lnet/minecraft/server/packs/PackType;Z)V", at = @At("RETURN"))
    private void yungsapi$addRepositorySourceFabric(PackType type, boolean forClientDataPackManager, CallbackInfo callback) {
        if (type == PackType.SERVER_DATA) {
            this.runtimeDataPackSource = new LocateReplacerDataPackResources.Source();
        } else if (type == PackType.CLIENT_RESOURCES) {
            this.runtimeDataPackSource = null;
        }
    }

    @Inject(method = "loadPacks", at = @At("RETURN"))
    private void yungsapi$loadPacksFabric(Consumer<Pack> consumer, CallbackInfo ci) {
        if (this.runtimeDataPackSource != null && this.type == PackType.SERVER_DATA) {
            this.runtimeDataPackSource.loadPacks(consumer);
        }
    }

}