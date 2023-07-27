package com.yungnickyoung.minecraft.yungsapi.autoregister;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Base class used to store a supplier for an AutoRegister entry.
 * Although possible to use this class directly, it is recommended to use one of the subclasses in the API
 * package if available.
 * @param <T> The type of the entry.
 */
public abstract class AutoRegisterEntry<T> {
    T cachedEntry;
    Supplier<T> entrySupplier;

    public AutoRegisterEntry(Supplier<T> entrySupplier) {
        this.entrySupplier = entrySupplier;
    }

    @ApiStatus.Internal
    public Supplier<T> getSupplier() {
        return entrySupplier;
    }

    @ApiStatus.Internal
    public void setSupplier(Supplier<T> entrySupplier) {
        this.entrySupplier = entrySupplier;
    }

    /**
     * Retrieves the cached entry if it exists, otherwise calls the supplier to create a new entry.
     * @return The cached entry, or a new entry if the cached entry does not exist.
     */
    public T get() {
        if (this.cachedEntry != null) return cachedEntry;
        T entry = entrySupplier.get();
        this.cachedEntry = entry;
        return entry;
    }
}