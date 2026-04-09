package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Internal representation of a field annotated with {@link AutoRegister},
 * used for creating and processing annotated fields internally.
 */
@ApiStatus.Internal
public class AutoRegisterField {
    public Object object;
    public Identifier name;
    public boolean processed;

    public AutoRegisterField(Object object, Identifier name) {
        this.object = object;
        this.name = name;
        this.processed = false;
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

    public void markProcessed() {
        this.processed = true;
    }
}