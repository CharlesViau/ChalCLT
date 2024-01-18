package Domain.CustumAnnotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SetPropertyEvent {
    String eventTriggerMethodName() default "";
    String validationMethodName() default "";
    Class<?> validationMethodLocation() default Void.class;
}
