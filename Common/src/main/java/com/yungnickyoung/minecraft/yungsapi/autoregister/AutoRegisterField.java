package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Internal representation of a field annotated with {@link AutoRegister},
 * used for creating and processing annotated fields internally.
 */
@ApiStatus.Internal
public class AutoRegisterField {
    public Object object;
    public Identifier name;
    public boolean processed;
    public Class<?> valueType;

    public AutoRegisterField(Object object, Identifier name) {
        this(object, name, object.getClass());
    }

    public AutoRegisterField(Object object, Identifier name, Type genericType) {
        this.object = object;
        this.name = name;
        this.processed = false;
        this.valueType = resolveValueType(genericType);
    }

    public Object object() {
        return this.object;
    }

    public Identifier name() {
        return this.name;
    }

    public boolean processed() {
        return this.processed;
    }

    public Class<?> valueType() {
        return this.valueType;
    }

    private static Class<?> resolveValueType(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return resolveValueType(parameterizedType.getActualTypeArguments()[0]);
        }
        if (type instanceof WildcardType wildcardType) {
            return resolveValueType(wildcardType.getUpperBounds()[0]);
        }
        return type instanceof Class<?> clazz ? clazz : Object.class;
    }

    public void markProcessed() {
        this.processed = true;
    }
}
