package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterFieldRouter;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.module.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public class NeoForgeAutoRegisterHelper implements IAutoRegisterHelper {
    @Override
    public void collectAllAutoRegisterFieldsInPackage(String packageName) {
        Map<Type, String> classToNamespaceMap = new HashMap<>();

        // Collect all AutoRegister annotations
        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> a.annotationType().equals(Type.getType(AutoRegister.class)))
                .toList();

        // First pass -> gather all modIds from class-level annotations.
        // Used for namespacing fields.
        annotations.stream()
                .filter(data -> data.targetType() == ElementType.TYPE)
                .forEach(data -> classToNamespaceMap.put(data.clazz(), (String) data.annotationData().get("value")));

        // Second pass -> scrape all annotated fields & queue for registration
        annotations.stream()
                .filter(data -> data.targetType() == ElementType.FIELD)
                .forEach(data -> {
                    // Check mod ID
                    String modId = classToNamespaceMap.get(data.clazz());
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

                    // Get field value
                    Object o;
                    try {
                        o = f.get(null);
                    } catch (IllegalAccessException e) {
                        // Impossible?
                        YungsApiCommon.LOGGER.error("Unable to get value for AutoRegister field {}. This shouldn't happen!", data.memberName());
                        throw new RuntimeException(e);
                    }

                    // Queue for registration
                    String name = (String) data.annotationData().get("value");
                    AutoRegisterField autoRegisterField = new AutoRegisterField(o, new ResourceLocation(modId, name));
                    AutoRegisterFieldRouter.queueField(autoRegisterField);
                });
    }

    @Override
    public void invokeAllAutoRegisterMethods(String packageName) {
        List<Method> methods = new ArrayList<>();

        // Collect all AutoRegister annotations
        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> a.annotationType().equals(Type.getType(AutoRegister.class)))
                .toList();

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

        PostLoadModuleNeoForge.METHODS.addAll(methods);
        PostLoadModuleNeoForge.init();
    }

    @Override
    public void processQueuedAutoRegEntries() {
        SoundEventModuleNeoForge.processEntries();
        StructurePieceTypeModuleNeoForge.processEntries();
        StructurePoolElementTypeModuleForge.processEntries();
        CriteriaModuleNeoForge.processEntries();
        StructureTypeModuleNeoForge.processEntries();
        FeatureModuleNeoForge.processEntries();
        PlacementModifierTypeModuleNeoForge.processEntries();
        CreativeModeTabModuleNeoForge.processEntries();
        ItemModuleNeoForge.processEntries();
        BlockModuleNeoForge.processEntries();
        BlockEntityTypeModuleNeoForge.processEntries();
        StructureProcessorTypeModuleNeoForge.processEntries();
        StructurePlacementTypeModuleNeoForge.processEntries();
        EntityTypeModuleNeoForge.processEntries();
        MobEffectModuleNeoForge.processEntries();
        PotionModuleNeoForge.processEntries();
        CommandModuleNeoForge.processEntries();
    }

    @Override
    public void registerBrewingRecipe(Supplier<Potion> inputPotion, Supplier<Item> ingredient, Supplier<Potion> outputPotion) {
        PotionModuleNeoForge.BrewingRecipe recipe = new PotionModuleNeoForge.BrewingRecipe(inputPotion, ingredient, outputPotion);
        PotionModuleNeoForge.BREWING_RECIPES.add(recipe);
    }

    @Override
    public void addCompostableItem(Supplier<Item> ingredient, float compostChance) {
        CompostModuleNeoForge.COMPOSTABLES.put(ingredient.get(), compostChance);
    }
}