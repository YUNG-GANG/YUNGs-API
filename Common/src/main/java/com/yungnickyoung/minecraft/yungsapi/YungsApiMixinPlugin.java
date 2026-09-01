package com.yungnickyoung.minecraft.yungsapi;// Created 2022-03-04T21:09:11

import com.yungnickyoung.minecraft.yungsapi.services.Services;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Currently unused
 * @author KJP12
 **/
public class YungsApiMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        // no-op
    }

    @Override
    public @Nullable String getRefMapperConfig() {
        // no-op, we have nothing to add
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Please be careful as to which classes you call here, as there is a possibility
        // of cascade loading Minecraft classes, *which you do not want.*
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // no-op
    }

    @Override
    public @Nullable List<String> getMixins() {
        // no-op, we have nothing to add
        return null;
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no-op
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no-op
    }
}
