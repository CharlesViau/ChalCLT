package Domain.CustumAnnotations;

import Domain.General.Components.Component;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AddObjectEvent {
    String eventTriggerMethodName() default "";

    Class<?>[] methodArgs() default {Component.class};

    String validationMethodName() default "";

    Class<?>[] validationMethodArgs() default {};
}
