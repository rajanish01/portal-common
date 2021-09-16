package com.epex.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permittable {

    public enum AccessLevel{
        ADMIN, EDITOR, READER
    }

    String groupId();

    AccessLevel accessLevel() default AccessLevel.READER;

}
