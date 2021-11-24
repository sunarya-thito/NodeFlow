package thito.nodeflow.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
/**
 * Any operation that using this annotation must be done in IO Thread
 */
public @interface IOThread {
}
