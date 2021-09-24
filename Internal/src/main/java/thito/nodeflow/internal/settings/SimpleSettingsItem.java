package thito.nodeflow.internal.settings;

import javafx.beans.property.*;
import thito.nodeflow.internal.language.*;

public class SimpleSettingsItem<T> extends SettingsItem<T> {
    private ObjectProperty<T> value;

    public SimpleSettingsItem(String key, I18n displayName, Class<T> type, T initialValue) {
        super(key, displayName, type, initialValue);
        this.value = new SimpleObjectProperty<>(initialValue);
    }

    @Override
    public ObjectProperty<T> valueProperty() {
        return value;
    }
}
