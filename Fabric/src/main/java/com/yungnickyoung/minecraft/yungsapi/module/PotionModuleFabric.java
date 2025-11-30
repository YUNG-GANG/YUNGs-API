package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PotionAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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
        Potion potion = autoRegisterPotion.get();

        // If the potion does not have a name, set it based on the annotation data
        if (((PotionAccessor) potion).getName() == null) {
            String name = data.name().getNamespace() + "." + data.name().getPath();
            ((PotionAccessor) potion).setName(name);
        }

        // Register potion
        Holder<Potion> holder = Registry.registerForHolder(BuiltInRegistries.POTION, data.name(), potion);
        autoRegisterPotion.setHolder(holder);

        data.markProcessed();
    }
}
