package thito.nodeflow.internal.settings.canvas;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Item {
    String value();
}
