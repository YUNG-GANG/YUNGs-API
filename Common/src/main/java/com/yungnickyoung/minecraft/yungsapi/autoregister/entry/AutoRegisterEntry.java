package com.yungnickyoung.minecraft.yungsapi.autoregister.entry;

import org.objectweb.asm.Type;

import java.util.function.Supplier;

public abstract class AutoRegisterEntry<T> {
    Supplier<T> entrySupplier;

    public AutoRegisterEntry(Supplier<T> entrySupplier) {
        this.entrySupplier = entrySupplier;
    }

    public Supplier<T> getSupplier() {
        return entrySupplier;
    }

    public void setSupplier(Supplier<T> entrySupplier) {
        this.entrySupplier = entrySupplier;
    }

    public T get() {
        return entrySupplier.get();
    }
}
