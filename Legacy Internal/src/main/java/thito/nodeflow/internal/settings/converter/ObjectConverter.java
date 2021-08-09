package thito.nodeflow.internal.settings.converter;

import thito.nodeflow.api.config.*;
import thito.nodeflow.api.settings.*;

public interface ObjectConverter<T> extends SettingsConverter<T> {

    @Override
    default void serialize(SettingsItem<T> requester, String name, Section section, T value) {
        section.set(value, name);
    }
}
