package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an item for processing via the AutoRegister system.
 * <br />
 * <br />
 * <p>
 * <b>Classes</b> with this annotation will be searched for annotated member fields and static methods.
 * A class-level {@link AutoRegister} annotation's value should always be your mod ID.
 * </p>
 * <br />
 * <p>
 * <b>Fields</b> with this annotation will be automatically registered at the appropriate time. If a field is annotated,
 * its encompassing class must also be annotated with {@link AutoRegister}.
 * </p>
 * <br />
 * <p>
 * <b>Methods</b> with this annotation will be automatically invoked after field registration is complete
 * (for Forge, this would be during CommonSetup). If a method is annotated,
 * its encompassing class must also be annotated with {@link AutoRegister}.
 * Note that methods with this annotation <i>must</i> be static and have no parameters.
 * </p>
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface AutoRegister {
    String value();
}