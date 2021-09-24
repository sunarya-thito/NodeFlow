package thito.nodeflow.internal.settings.canvas;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Category {
    String value();
    SettingsContext context() default SettingsContext.ALL;
}
