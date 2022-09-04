package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public class AutoRegisterPotion extends AutoRegisterEntry<Potion> {
    private MobEffectInstance mobEffectInstance;

    public static AutoRegisterPotion mobEffect(MobEffectInstance mobEffectInstance) {
        return new AutoRegisterPotion(null, mobEffectInstance);
    }

    private AutoRegisterPotion(Supplier<Potion> potionSupplier, MobEffectInstance mobEffectInstance) {
        super(potionSupplier);
        this.mobEffectInstance = mobEffectInstance;
    }

    public MobEffectInstance getMobEffectInstance() {
        return mobEffectInstance;
    }
}
