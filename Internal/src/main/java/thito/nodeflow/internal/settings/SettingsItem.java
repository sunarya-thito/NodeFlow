package thito.nodeflow.internal.settings;

import javafx.beans.property.*;
import thito.nodeflow.internal.language.*;

public abstract class SettingsItem<T> {
    private String key;
    private I18n displayName;
    private T initialValue;
    private Class<T> type;

    public SettingsItem(String key, I18n displayName, Class<T> type, T initialValue) {
        this.key = key;
        this.displayName = displayName;
        this.initialValue = initialValue;
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public T getInitialValue() {
        return initialValue;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public SettingsProperty<T> createProperty() {
        return new SettingsProperty<>(initialValue, this);
    }

    public void setValue(T value) {
        valueProperty().setValue(value);
    }

    public T getValue() {
        return valueProperty().getValue();
    }

    public abstract Property<T> valueProperty();

}
