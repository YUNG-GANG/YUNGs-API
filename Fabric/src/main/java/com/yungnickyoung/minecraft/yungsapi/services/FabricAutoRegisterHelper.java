package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterFieldRouter;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PotionBrewingAccessor;
import com.yungnickyoung.minecraft.yungsapi.module.*;
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

        // Scrape all annotated fields & queue for registration
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
                            YungsApiCommon.LOGGER.error("Unable to get value for field {}. This shouldn't happen!", name);
                            throw new RuntimeException(e);
                        } catch (NullPointerException e) {
                            String message = String.format("Attempted to 'get' Field with null object. " +
                                    "Did you forget to include a 'static' modifier for field '%s'?", name);
                            throw new RuntimeException(message);
                        }
                        ResourceLocation resourceLocation = new ResourceLocation(modId, name);
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
                    } catch (NullPointerException e) {
                        String message = String.format("Attempted to invoke AutoRegister method with null object. " +
                                "Did you forget to include a 'static' modifier for method '%s'?", method.getName());
                        throw new RuntimeException(message);
                    }
                }));
    }

    @Override
    public void processQueuedAutoRegEntries() {
        CreativeModeTabModuleFabric.processEntries();
        SoundEventModuleFabric.processEntries();
        StructurePoolElementTypeModuleFabric.processEntries();
        CriteriaModuleFabric.processEntries();
        ItemModuleFabric.processEntries();
        BlockModuleFabric.processEntries();
        StructureFeatureModuleFabric.processEntries();
        FeatureModuleFabric.processEntries();
        ConfiguredFeatureModuleFabric.processEntries();
        PlacedFeatureModuleFabric.processEntries();
        StructureProcessorTypeModuleFabric.processEntries();
        BlockEntityTypeModuleFabric.processEntries();
        ParticleTypeModuleFabric.processEntries();
        BiomeModuleFabric.processEntries();
        EntityTypeModuleFabric.processEntries();
        MobEffectModuleFabric.processEntries();
        PotionModuleFabric.processEntries();
        CommandModuleFabric.processEntries();
    }

    @Override
    public void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion) {
        PotionBrewingAccessor.callAddMix(inputPotion.get(), ingredient.get(), outputPotion.get());
    }
}
