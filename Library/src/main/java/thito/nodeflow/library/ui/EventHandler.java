package thito.nodeflow.library.ui;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    String id();
    FXEvent event();
}
