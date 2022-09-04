package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterFieldRouter;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PotionBrewingAccessor;
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
    private Set<Class<?>> annotatedClasses;

    @Override
    public void prepareAllAutoRegisterFields() {
        // Collect all class-level annotations
        if (this.annotatedClasses == null) {
            Reflections reflections = new Reflections(
                    new ConfigurationBuilder()
                            .forPackage("com.yungnickyoung.minecraft")
                            .filterInputsBy(new FilterBuilder().includePackage("com.yungnickyoung.minecraft"))
                            .setScanners(Scanners.TypesAnnotated));
            this.annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);
        }

        // Scrape all annotated fields & queue for registration
        annotatedClasses.forEach(clazz -> {
            String modId = clazz.getAnnotation(AutoRegister.class).value();
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(AutoRegister.class))
                    .forEach(f -> {
                        f.setAccessible(true);
                        String name = f.getAnnotation(AutoRegister.class).value();
                        Object o;
                        try {
                            o = f.get(null);
                        } catch (IllegalAccessException e) {
                            // Impossible?
                            throw new RuntimeException(e);
                        }
                        ResourceLocation resourceLocation = new ResourceLocation(modId, name);
                        AutoRegisterField registerData = new AutoRegisterField(o, resourceLocation);
                        AutoRegisterFieldRouter.queueField(registerData);
                    });
        });
    }

    @Override
    public void invokeAllAutoRegisterMethods() {
        // Collect all class-level annotations
        if (this.annotatedClasses == null) {
            Reflections reflections = new Reflections(
                    new ConfigurationBuilder()
                            .forPackage("com.yungnickyoung.minecraft")
                            .filterInputsBy(new FilterBuilder().includePackage("com.yungnickyoung.minecraft"))
                            .setScanners(Scanners.TypesAnnotated));
            this.annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);
        }

        // Scan & invoke all annotated methods
        annotatedClasses.forEach(clazz -> {
            Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(AutoRegister.class))
                    .forEach(m -> {
                        m.setAccessible(true);
                        try {
                            m.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            YungsApiCommon.LOGGER.error("Unable to invoke method {} - make sure it's static and has no args!", m.getName());
                            throw new RuntimeException(e);
                        }
                    });
        });
    }

    @Override
    public void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion) {
        PotionBrewingAccessor.callAddMix(inputPotion.get(), ingredient.get(), outputPotion.get());
    }
}
