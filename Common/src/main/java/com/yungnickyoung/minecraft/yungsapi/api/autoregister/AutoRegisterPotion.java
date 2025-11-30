package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Wrapper for registering {@link Potion}s with AutoRegister.
 * <br />
 * Example usage:
 * <pre>
 * {@code
 * @AutoRegister("frost")
 * public static final AutoRegisterPotion FROST_POTION = AutoRegisterPotion
 *        .mobEffect(() -> new MobEffectInstance(MobEffectModule.FROZEN_EFFECT.get(), 0, 0, false, true, false));
 * }
 * </pre>
 */
public class AutoRegisterPotion extends AutoRegisterEntry<Potion> {
    public static AutoRegisterPotion of(Supplier<Potion> potionSupplier) {
        return new AutoRegisterPotion(potionSupplier);
    }

    private AutoRegisterPotion(Supplier<Potion> potionSupplier) {
        super(potionSupplier);
    }

    private Holder<Potion> holder;

    public Holder<Potion> getHolder() {
        if (holder == null) {
            throw new IllegalStateException("Potion holder is not set. Ensure the Potion is registered before accessing the holder.");
        }
        return holder;
    }

    @ApiStatus.Internal
    public void setHolder(Holder<Potion> holder) {
        this.holder = holder;
    }
}
