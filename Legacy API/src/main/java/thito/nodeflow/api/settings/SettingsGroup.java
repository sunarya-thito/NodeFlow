package thito.nodeflow.api.settings;

import thito.nodeflow.api.locale.*;

import java.util.*;

public interface SettingsGroup {
    String name();
    I18nItem getDisplay();
    List<SettingsItem<?>> getItems();
    default SettingsGroup addItems(SettingsItem<?>... items) {
        for (SettingsItem<?> item : items) {
            getItems().add(item);
        }
        return this;
    }
}
