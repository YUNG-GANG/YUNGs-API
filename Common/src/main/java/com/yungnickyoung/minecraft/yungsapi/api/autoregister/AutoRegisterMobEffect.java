package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

/**
 * Wrapper for registering {@link MobEffect}s with AutoRegister.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 * @AutoRegister("frost")
 * public static final AutoRegisterMobEffect FROZEN_EFFECT = AutoRegisterMobEffect
 *         .of(() -> new FrostMobEffect(200, 100, 600));
 * }
 * </pre>
 */
public class AutoRegisterMobEffect extends AutoRegisterEntry<MobEffect> {
    public static AutoRegisterMobEffect of(Supplier<MobEffect> mobEffectSupplier) {
        return new AutoRegisterMobEffect(mobEffectSupplier);
    }

    private AutoRegisterMobEffect(Supplier<MobEffect> mobEffectSupplier) {
        super(mobEffectSupplier);
    }
}
