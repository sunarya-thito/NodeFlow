package thito.nodeflow.internal.resource;

import javafx.event.*;

public class ResourceEvent extends Event {
    public static final EventType<ResourceEvent> FILE_MODIFIED = new EventType<>(ANY, "file_modified");
    private Resource resource;

    public ResourceEvent(EventType<ResourceEvent> eventType, Resource resource) {
        super(eventType);
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }
}
