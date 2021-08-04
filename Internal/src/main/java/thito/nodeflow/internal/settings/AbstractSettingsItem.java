package thito.nodeflow.internal.settings;

import com.dlsc.preferencesfx.model.*;
import javafx.beans.property.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.settings.*;

public abstract class AbstractSettingsItem<T> implements SettingsItem<T> {

    private String name;
    private I18nItem displayName;
    private ObjectProperty<T> valueProperty = new SimpleObjectProperty<>();
    private T defaultValue;
    private SettingsConverter<T> converter;
    private SettingsValidator validator;

    public AbstractSettingsItem(String name, I18nItem displayName, T defaultValue, SettingsConverter<T> converter) {
        this.name = name;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.converter = converter;
        valueProperty.set(getDefaultValue());
    }

    @Override
    public SettingsValidator getValidator() {
        return validator;
    }

    @Override
    public void setValidator(SettingsValidator validator) {
        this.validator = validator;
    }

    @Override
    public ObjectProperty<T> impl_valueProperty() {
        return valueProperty;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public I18nItem getDisplayName() {
        return displayName;
    }

    @Override
    public T getValue() {
        return valueProperty.get() == null ? getDefaultValue() : valueProperty.get();
    }

    @Override
    public T setValue(T value) {
        T old = valueProperty.get();
        valueProperty.set(value);
        return old;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public SettingsConverter<T> getConverter() {
        return converter;
    }

    public abstract Setting createSetting();
}
