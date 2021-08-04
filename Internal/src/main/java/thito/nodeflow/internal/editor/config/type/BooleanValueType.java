package thito.nodeflow.internal.editor.config.type;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.config.*;
import thito.nodeflow.library.binding.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

public class BooleanValueType implements ConfigValueType {
    @Override
    public String getId() {
        return "boolean";
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("config-boolean-type");
    }

    @Override
    public Class<?> getFieldType() {
        return boolean.class;
    }

    @Override
    public ConfigValueHandler createHandler(Object value) {
        Boolean bool;
        if (value instanceof Boolean) {
            bool = (Boolean) value;
        } else {
            bool = Boolean.parseBoolean(String.valueOf(value));
        }
        return new ConfigValueHandler() {
            private CheckBox checkBox = new CheckBox();
            private ObjectProperty<Object> val = new SimpleObjectProperty<>(bool);

            {
                checkBox.getStyleClass().add("boolean-value-type");
                MappedBidirectionalBinding.bindBidirectional(checkBox.selectedProperty(), val, x -> x, x -> x instanceof Boolean ? (Boolean) x : false);
            }

            @Override
            public ObjectProperty<Object> valueProperty() {
                return val;
            }

            @Override
            public ConfigValueType getType() {
                return BooleanValueType.this;
            }

            @Override
            public void save(String key, Section section) {
                section.set(key, val.get());
            }

            @Override
            public Node impl_getPeer() {
                return checkBox;
            }

            @Override
            public Reference getCompilableReference() {
                return val.get() instanceof Boolean && (Boolean) val.get() ? Java.Logic.True() : Java.Logic.False();
            }

        };
    }
}
