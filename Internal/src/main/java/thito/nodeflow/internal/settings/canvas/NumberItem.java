package thito.nodeflow.internal.settings.canvas;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumberItem {
    double min() default Double.MIN_VALUE;
    double max() default Double.MAX_VALUE;
}
