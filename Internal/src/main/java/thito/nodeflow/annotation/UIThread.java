package thito.nodeflow.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
/**
 * Any operation that using this annotation must be done in UI Thread
 */
public @interface UIThread {
}
