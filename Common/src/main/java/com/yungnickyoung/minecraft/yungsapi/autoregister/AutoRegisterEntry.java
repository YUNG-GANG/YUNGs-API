package com.yungnickyoung.minecraft.yungsapi.autoregister;

import java.util.function.Supplier;

public abstract class AutoRegisterEntry<T> {
    T cachedEntry;
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
        if (this.cachedEntry != null) return cachedEntry;
        T entry = entrySupplier.get();
        this.cachedEntry = entry;
        return entry;
    }
}
