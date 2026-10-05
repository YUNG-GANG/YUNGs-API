package com.yungnickyoung.minecraft.yungsapi.module;
import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PotionAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registration of Potions and brewing recipes.
 */
public class PotionModuleNeoForge {
    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildAutoRegistrar(
                Registries.POTION,
                AutoRegistrationManager.POTIONS,
                PotionModuleNeoForge::buildPotion,
                PotionModuleNeoForge::registerPotion)
        );

    }

    private static Potion buildPotion(AutoRegisterField data) {
        AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion) data.object();
        Potion potion = autoRegisterPotion.get();

        // If the potion does not have a name, set it based on the annotation data
        if (((PotionAccessor) potion).getName() == null) {
            String name = data.name().getNamespace() + "." + data.name().getPath();
            ((PotionAccessor) potion).setName(name);
        }

        return potion;
    }

    private static void registerPotion(AutoRegisterField data, Potion potion, RegisterEvent.RegisterHelper<Potion> helper) {
        // We directly reference the registry instead of using the helper so we can set the holder on the AutoRegisterPotion instance.
        // At the time of writing, the helper does not provide a way to get the holder after registration.
        Holder<Potion> holder = Registry.registerForHolder(BuiltInRegistries.POTION, data.name(), potion);
        ((AutoRegisterPotion) data.object()).setHolder(holder);
    }

}
