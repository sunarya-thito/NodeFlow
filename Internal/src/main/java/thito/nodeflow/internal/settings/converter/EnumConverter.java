package thito.nodeflow.internal.settings.converter;

import thito.nodeflow.api.config.*;
import thito.nodeflow.api.settings.*;

public class EnumConverter<T extends Enum<T>> implements SettingsConverter<T> {

    private Class<T> enumClass;

    public EnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(SettingsItem<T> requester, String name, Section section) {
        return Enum.valueOf(enumClass, section.getString(name));
    }

    @Override
    public void serialize(SettingsItem<T> requester, String name, Section section, T value) {
        section.set(value.name(), name);
    }
}
