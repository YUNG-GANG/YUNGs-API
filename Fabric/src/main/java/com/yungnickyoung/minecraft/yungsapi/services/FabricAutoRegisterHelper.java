package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterFieldRouter;
import com.yungnickyoung.minecraft.yungsapi.module.*;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;

public class FabricAutoRegisterHelper implements IAutoRegisterHelper {
    @Override
    public void collectAllAutoRegisterFieldsInPackage(String packageName) {
        // Collect all AutoRegister annotated classes in package
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(packageName)
                        .filterInputsBy(new FilterBuilder().includePackage(packageName))
                        .setScanners(Scanners.TypesAnnotated));
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);

        annotatedClasses.forEach(clazz -> {
            String modId = clazz.getAnnotation(AutoRegister.class).value();

            // Scan each class for AutoRegister annotated fields and queue them for registration
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(AutoRegister.class))
                    .forEach(field -> {
                        field.setAccessible(true);
                        String name = field.getAnnotation(AutoRegister.class).value();
                        Object o;
                        try {
                            o = field.get(null);
                        } catch (IllegalAccessException e) {
                            // Impossible?
                            throw new RuntimeException(e);
                        }
                        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(modId, name);
                        AutoRegisterField autoRegisterField = new AutoRegisterField(o, resourceLocation);
                        AutoRegisterFieldRouter.queueField(autoRegisterField);
                    });
        });
    }

    @Override
    public void invokeAllAutoRegisterMethods(String packageName) {
        // Collect all AutoRegister annotated classes in package
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(packageName)
                        .filterInputsBy(new FilterBuilder().includePackage(packageName))
                        .setScanners(Scanners.TypesAnnotated));
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);

        // Scan each class for AutoRegister annotated methods and invoke them
        annotatedClasses.forEach(clazz -> Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AutoRegister.class))
                .forEach(method -> {
                    method.setAccessible(true);
                    try {
                        method.invoke(null);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        YungsApiCommon.LOGGER.error("Unable to invoke method {} - make sure it's static and has no args!", method.getName());
                        throw new RuntimeException(e);
                    }
                }));
    }

    @Override
    public void processQueuedAutoRegEntries() {
        SoundEventModuleFabric.processEntries();
        StructurePieceTypeModuleFabric.processEntries();
        StructurePoolElementTypeModuleFabric.processEntries();
        CriteriaModuleFabric.processEntries();
        StructureTypeModuleFabric.processEntries();
        FeatureModuleFabric.processEntries();
        PlacementModifierTypeModuleFabric.processEntries();
        ItemModuleFabric.processEntries();
        BlockModuleFabric.processEntries();
        CreativeModeTabModuleFabric.processEntries();
        BlockEntityTypeModuleFabric.processEntries();
        StructureProcessorTypeModuleFabric.processEntries();
        StructurePlacementTypeModuleFabric.processEntries();
        EntityTypeModuleFabric.processEntries();
        MobEffectModuleFabric.processEntries();
        PotionModuleFabric.processEntries();
        CommandModuleFabric.processEntries();
    }

    @Override
    public void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion) {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> builder.addMix(Holder.direct(inputPotion.get()), ingredient.get(), Holder.direct(outputPotion.get())));
    }

    @Override
    public void addCompostableItem(Supplier<Item> ingredient, float compostChance) {
        CompostingChanceRegistry.INSTANCE.add(ingredient.get(), compostChance);
    }
}