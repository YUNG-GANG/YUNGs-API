package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public interface IAutoRegisterHelper {
    /**
     * Invokes all {@link AutoRegister} annotated methods within the specified package.
     * <p>
     * This should typically be performed after calls to {@link IAutoRegisterHelper#collectAllAutoRegisterFieldsInPackage}
     * and {@link IAutoRegisterHelper#processQueuedAutoRegEntries()} in case you need to access any fields using the
     * AutoRegister system.
     * </p>
     * <b>All {@link AutoRegister} annotated methods must be static and have no arguments!</b>
     *
     * @param packageName Name of a package containing {@link AutoRegister} annotated methods.
     *                    When specifying a package, try to be as precise as possible,
     *                    as all subpackages will also be recursively scanned.
     *                    <b>Note that on Forge, all annotations are processed up front, and as such this parameter
     *                    is not used.</b>
     */
    void invokeAllAutoRegisterMethods(String packageName);

    /**
     * Scans all {@link AutoRegister} annotated fields within the specified package and queues them for registration,
     * independent of mod loader. Actual registration of fields is not performed until
     * {@link IAutoRegisterHelper#processQueuedAutoRegEntries()} is called.
     *
     * @param packageName Name of a package containing {@link AutoRegister} annotated fields.
     *                    When specifying a package, try to be as precise as possible,
     *                    as all subpackages will also be recursively scanned.
     *                    <b>Note that on Forge, all annotations are processed up front, and as such this parameter
     *                    is not used.</b>
     */
    void collectAllAutoRegisterFieldsInPackage(String packageName);

    /**
     * Processes all AutoRegister fields that have been queued for registration.
     * <p>
     * On Fabric, this will register all queued fields right away.
     * </p>
     * <p>
     * On Forge, this will register event listeners and defer field registration to execute during the proper events.
     * </p>
     */
    void processQueuedAutoRegEntries();

    void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion);

    void addCompostableItem(Supplier<Item> ingredient, float compostChance);
}
