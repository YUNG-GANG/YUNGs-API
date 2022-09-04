package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class AutoRegisterMobEffect extends AutoRegisterEntry<MobEffect> {
    public static AutoRegisterMobEffect of(Supplier<MobEffect> mobEffectSupplier) {
        return new AutoRegisterMobEffect(mobEffectSupplier);
    }

    private AutoRegisterMobEffect(Supplier<MobEffect> mobEffectSupplier) {
        super(mobEffectSupplier);
    }
}
