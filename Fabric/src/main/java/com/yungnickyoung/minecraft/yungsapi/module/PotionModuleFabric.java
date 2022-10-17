package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

/**
 * Registration of Potions.
 */
public class PotionModuleFabric {
    public static void processEntries() {
        AutoRegistrationManager.POTIONS.stream()
                .filter(data -> !data.processed())
                .forEach(PotionModuleFabric::register);
    }

    private static void register(AutoRegisterField data) {
        AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion) data.object();
        MobEffectInstance mobEffectInstance = autoRegisterPotion.getMobEffectInstance();
        String name = data.name().getNamespace() + "." + data.name().getPath();
        Potion potion = new Potion(name, mobEffectInstance);
        autoRegisterPotion.setSupplier(() -> potion);

        // Register mob effect
        Registry.register(Registry.POTION, data.name(), potion);
        data.markProcessed();
    }
}
