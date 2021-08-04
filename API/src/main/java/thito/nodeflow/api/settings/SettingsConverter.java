package thito.nodeflow.api.settings;

import thito.nodeflow.api.config.Section;

public interface SettingsConverter<T> {
    T deserialize(SettingsItem<T> requester, String name, Section section);

    void serialize(SettingsItem<T> requester, String name, Section section, T value);
}
