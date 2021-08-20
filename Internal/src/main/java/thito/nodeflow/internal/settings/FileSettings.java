package thito.nodeflow.internal.settings;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FileSettings {
    boolean mustExist() default true;
    boolean directory() default false;
    boolean save() default false;
    String[] filters() default {"Any *"};
}
