package thito.nodeflow.api.settings;

import javafx.beans.property.ObjectProperty;
import javafx.collections.*;
import thito.nodeflow.api.locale.I18nItem;

public interface SettingsItem<T> {
    String name();

    I18nItem getDisplayName();

    SettingsValidator getValidator();

    void setValidator(SettingsValidator validator);

    T getValue();

    T setValue(T value);

    ObjectProperty<T> impl_valueProperty();

    T getDefaultValue();

    ObservableList<T> getPossibleValues();

    SettingsConverter<T> getConverter();
}
