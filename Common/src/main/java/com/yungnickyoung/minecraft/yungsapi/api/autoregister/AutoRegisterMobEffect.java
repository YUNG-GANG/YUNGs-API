package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.ApiStatus;

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

    private Holder<MobEffect> holder;

    public Holder<MobEffect> getHolder() {
        if (holder == null) {
            throw new IllegalStateException("MobEffect holder is not set. Ensure the MobEffect is registered before accessing the holder.");
        }
        return holder;
    }

    @ApiStatus.Internal
    public void setHolder(Holder<MobEffect> holder) {
        this.holder = holder;
    }

    private AutoRegisterMobEffect(Supplier<MobEffect> mobEffectSupplier) {
        super(mobEffectSupplier);
    }
}
