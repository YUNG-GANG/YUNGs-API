package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterData;
import com.yungnickyoung.minecraft.yungsapi.autoregister.RegisterDataRouter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.*;

public class ForgeAutoRegisterHelper implements IAutoRegisterHelper {
    @Override
    public void autoRegisterAllObjects(List<RegisterData> allRegisterData) {
        allRegisterData.forEach(RegisterDataRouter::queueRegisterData);
    }

    @Override
    public List<RegisterData> getAllAutoRegisterFields() {
        List<RegisterData> allAutoRegisterData = new ArrayList<>();
        Map<Type, String> classModIds = new HashMap<>(); // Map of class to namespace

        final List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> a.annotationType().equals(Type.getType(AutoRegister.class)))
                .toList();

        // First pass -> gather all modIds from class-level annotations
        annotations.stream()
                .filter(data -> data.targetType() == ElementType.TYPE)
                .forEach(data -> classModIds.put(data.clazz(), (String) data.annotationData().get("value")));

        // Second pass -> store values for registration from field-level annotations
        annotations.stream()
                .filter(data -> data.targetType() == ElementType.FIELD)
                .forEach(data -> {
                    String modId = classModIds.get(data.clazz());
                    if (modId == null) {
                        YungsApiCommon.LOGGER.error("Missing class AutoRegister annotation for field {}", data.memberName());
                        return;
                    }
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(data.clazz().getClassName(), false, AutoRegistrationManager.class.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        // Impossible?
                        throw new RuntimeException(e);
                    }
                    Field f;
                    try {
                        f = clazz.getDeclaredField(data.memberName());
                    } catch (NoSuchFieldException e) {
                        // Impossible?
                        throw new RuntimeException(e);
                    }
                    Object o;
                    try {
                        o = f.get(null);
                    } catch (IllegalAccessException e) {
                        // Impossible?
                        throw new RuntimeException(e);
                    }
                    RegisterData registerData = new RegisterData(o, new ResourceLocation(modId, (String) data.annotationData().get("value")));
                    allAutoRegisterData.add(registerData);
                });

        return allAutoRegisterData;
    }
}
