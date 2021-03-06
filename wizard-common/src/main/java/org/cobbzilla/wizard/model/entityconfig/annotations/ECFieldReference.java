package org.cobbzilla.wizard.model.entityconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.cobbzilla.wizard.model.entityconfig.EntityFieldReference.REF_PARENT;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
public @interface ECFieldReference {
    String control() default "hidden";
    String refEntity() default REF_PARENT;
    String refField() default "uuid";
    String refDisplayField() default "name";
    String refFinder() default "";
}
