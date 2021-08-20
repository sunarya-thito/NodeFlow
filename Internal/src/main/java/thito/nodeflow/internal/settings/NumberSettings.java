package thito.nodeflow.internal.settings;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumberSettings {
    double min() default Double.MIN_VALUE;
    double max() default Double.MAX_VALUE;
}
