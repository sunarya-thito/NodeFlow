package thito.nodeflow.internal.plugin.event;

public @interface EventHandler {
    EventPriority priority() default EventPriority.NORMAL;
    boolean ignoreCancelled() default false;
}
