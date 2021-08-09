package thito.nodeflow.api.project.property;

public interface ComponentPropertyType<T> {
    ComponentPropertyHandler<T> createHandler(ComponentProperty property);
}
