package thito.nodeflow.internal.node.property;

import javafx.beans.property.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.property.*;

public class ComponentPropertyImpl<T> implements ComponentProperty<T> {
    private Property<T> value;
    private ComponentPropertyHandler<T> handler;
    private ComponentPropertyUI peer;
    private I18nItem name;
    public ComponentPropertyImpl(I18nItem name, Property<T> property, ComponentPropertyType<T> type) {
        this.name = name;
        value = property;
        handler = type.createHandler(this);
        peer = new ComponentPropertyUI(this);
    }

    @Override
    public BooleanProperty disableProperty() {
        return handler.disableProperty();
    }

    @Override
    public I18nItem getName() {
        return name;
    }

    @Override
    public Property<T> valueProperty() {
        return value;
    }

    @Override
    public ComponentPropertyHandler<T> getHandler() {
        return handler;
    }

    @Override
    public ComponentPropertyUI impl_getPeer() {
        return peer;
    }
}
