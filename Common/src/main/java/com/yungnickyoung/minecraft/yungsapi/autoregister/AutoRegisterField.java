package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * Internal representation of a field annotated with {@link AutoRegister},
 * used for creating and processing annotated fields internally.
 */
@ApiStatus.Internal
public class AutoRegisterField {
    public Object object;
    public ResourceLocation name;
    public boolean processed;

    public AutoRegisterField(Object object, ResourceLocation name) {
        this.object = object;
        this.name = name;
        this.processed = false;
    }

    public Object object() {
        return this.object;
    }

    public ResourceLocation name() {
        return this.name;
    }

    public boolean processed() {
        return this.processed;
    }

    public void markProcessed() {
        this.processed = true;
    }
}