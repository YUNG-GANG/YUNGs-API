package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterFieldRouter;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.module.PostLoadModuleForge;
import com.yungnickyoung.minecraft.yungsapi.module.PotionModuleForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public class ForgeAutoRegisterHelper implements IAutoRegisterHelper {
    List<ModFileScanData.AnnotationData> annotations;

    @Override
    public void prepareAllAutoRegisterFields() {
        Map<Type, String> classModIds = new HashMap<>(); // Map of class to namespace

        // Collect all annotations
        if (this.annotations == null) {
            this.annotations = ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getAnnotations)
                    .flatMap(Collection::stream)
                    .filter(a -> a.annotationType().equals(Type.getType(AutoRegister.class)))
                    .toList();
        }

        // First pass -> gather all modIds from class-level annotations.
        // Used for naming fields.
        annotations.stream()
                .filter(data -> data.targetType() == ElementType.TYPE)
                .forEach(data -> classModIds.put(data.clazz(), (String) data.annotationData().get("value")));

        // Second pass -> scrape all annotated fields & queue for registration
        annotations.stream()
                .filter(data -> data.targetType() == ElementType.FIELD)
                .forEach(data -> {
                    // Check mod ID
                    String modId = classModIds.get(data.clazz());
                    if (modId == null) {
                        YungsApiCommon.LOGGER.error("Missing class AutoRegister annotation for field {}", data.memberName());
                        return;
                    }

                    // Get containing class
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(data.clazz().getClassName(), false, AutoRegistrationManager.class.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        YungsApiCommon.LOGGER.error("Unable to find class containing AutoRegister field {}. This shouldn't happen!", data.memberName());
                        YungsApiCommon.LOGGER.error("If you're using AutoRegister on a field, make sure the containing class is also using the AutoRegister annotation with your mod ID as the value.");
                        throw new RuntimeException(e);
                    }

                    // Get field
                    Field f;
                    try {
                        f = clazz.getDeclaredField(data.memberName());
                    } catch (NoSuchFieldException e) {
                        YungsApiCommon.LOGGER.error("Unable to find AutoRegister field with name {} in class {}. This shouldn't happen!", data.memberName(), clazz.getName());
                        throw new RuntimeException(e);
                    }
                    f.setAccessible(true);

                    // Get field value
                    Object o;
                    try {
                        o = f.get(null);
                    } catch (IllegalAccessException e) {
                        // Impossible?
                        YungsApiCommon.LOGGER.error("Unable to get value for fields {}. This shouldn't happen!", data.memberName());
                        throw new RuntimeException(e);
                    }

                    // Queue for registration
                    String name = (String) data.annotationData().get("value");
                    AutoRegisterField registerData = new AutoRegisterField(o, new ResourceLocation(modId, name));
                    AutoRegisterFieldRouter.queueField(registerData);
                });
    }

    @Override
    public void invokeAllAutoRegisterMethods() {
        List<Method> methods = new ArrayList<>();

        // Collect all annotations
        if (this.annotations == null) {
            this.annotations = ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getAnnotations)
                    .flatMap(Collection::stream)
                    .filter(a -> a.annotationType().equals(Type.getType(AutoRegister.class)))
                    .toList();
        }

        // Scan all annotated methods
        annotations.stream()
                .filter(data -> data.targetType() == ElementType.METHOD)
                .forEach(data -> {
                    // Get containing class
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(data.clazz().getClassName(), false, AutoRegistrationManager.class.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        YungsApiCommon.LOGGER.error("Unable to find class containing AutoRegister method {}. This shouldn't happen!", data.memberName());
                        YungsApiCommon.LOGGER.error("If you're using AutoRegister on a method, make sure the containing class is also using the AutoRegister annotation with your mod ID as the value.");
                        throw new RuntimeException(e);
                    }

                    // Get method
                    Method m;
                    try {
                        m = clazz.getDeclaredMethod(data.memberName().substring(0, data.memberName().indexOf("(")));
                    } catch (NoSuchMethodException e) {
                        YungsApiCommon.LOGGER.error("Unable to find AutoRegister method with name {} in class {}. This shouldn't happen!", data.memberName(), clazz.getName());
                        throw new RuntimeException(e);
                    }
                    m.setAccessible(true);
                    methods.add(m);
                });

        PostLoadModuleForge.METHODS = methods;
        PostLoadModuleForge.init();
    }

    @Override
    public void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion) {
        PotionModuleForge.BrewingRecipe recipe = new PotionModuleForge.BrewingRecipe(inputPotion, ingredient, outputPotion);
        PotionModuleForge.BREWING_RECIPES.add(recipe);
    }
}
