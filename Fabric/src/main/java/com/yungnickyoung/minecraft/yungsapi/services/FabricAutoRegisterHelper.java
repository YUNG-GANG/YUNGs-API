package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterDataRouter;
import net.minecraft.resources.ResourceLocation;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FabricAutoRegisterHelper implements IAutoRegisterHelper {
    @Override
    public void autoRegisterAllObjects(List<RegisterData> allRegisterData) {
        allRegisterData.forEach(RegisterDataRouter::queueRegisterData);
    }

    @Override
    public List<RegisterData> getAllAutoRegisterFieldsInPackage(String packageName) {
        List<RegisterData> allAutoRegisterData = new ArrayList<>();

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(packageName)
                        .filterInputsBy(new FilterBuilder().includePackage(packageName))
                        .setScanners(Scanners.TypesAnnotated));
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);

        annotatedClasses.stream().forEach(clazz -> {
            String modId = clazz.getAnnotation(AutoRegister.class).value();
            Arrays.stream(clazz.getDeclaredFields())
                    .peek(f -> f.setAccessible(true))
                    .filter(field -> field.isAnnotationPresent(AutoRegister.class))
                    .forEach(f -> {
                        String name = f.getAnnotation(AutoRegister.class).value();
                        Object o;
                        try {
                            o = f.get(null);
                        } catch (IllegalAccessException e) {
                            // Impossible?
                            throw new RuntimeException(e);
                        }
                        ResourceLocation resourceLocation = new ResourceLocation(modId, name);
                        RegisterData registerData = new RegisterData(o, resourceLocation);
                        allAutoRegisterData.add(registerData);
                    });
        });
        return allAutoRegisterData;
    }

    @Override
    public void processAllAutoRegEntriesForPackage(String packageName) {
        AutoRegistrationManager.registerAnnotationsInPackage(packageName);
        FabricModulesLoader.processModuleEntries();
    }
}