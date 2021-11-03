package thito.nodeflow.internal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
/**
 * Any operation that using this annotation must be done in Background Thread
 */
public @interface BGThread {
}
