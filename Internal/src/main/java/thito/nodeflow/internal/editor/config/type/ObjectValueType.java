package thito.nodeflow.internal.editor.config.type;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.config.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class ObjectValueType implements ConfigValueType {
    private Class<?> type;
    private I18nItem displayName;

    public ObjectValueType(Class<?> type, I18nItem displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    @Override
    public Class<?> getFieldType() {
        return type;
    }

    @Override
    public String getId() {
        return type.getName();
    }

    @Override
    public I18nItem getDisplayName() {
        return displayName;
    }

    @Override
    public ConfigValueHandler createHandler(Object value) {
        return new ConfigValueHandler() {
            private Label box = new Label(name(type));

            private String name(Class<?> type) {
                if (type.isArray()) {
                    return name(type.getComponentType()) + "[]";
                }
                return type.getName();
            }
            {
                box.getStyleClass().add("object-value-type");
            }

            private ObjectProperty<Object> object = new SimpleObjectProperty<>();

            @Override
            public ObjectProperty<Object> valueProperty() {
                return object;
            }

            @Override
            public ConfigValueType getType() {
                return ObjectValueType.this;
            }

            @Override
            public void save(String key, Section section) {
            }

            @Override
            public Node impl_getPeer() {
                return box;
            }

            @Override
            public Reference getCompilableReference() {
                return Java.Class(type).newInstance();
            }

        };
    }
}
