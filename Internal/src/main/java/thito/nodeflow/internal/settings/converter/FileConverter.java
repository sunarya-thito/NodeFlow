package thito.nodeflow.internal.settings.converter;

import thito.nodeflow.api.config.*;
import thito.nodeflow.api.settings.*;

import java.io.*;

public class FileConverter implements SettingsConverter<File> {

    public static final FileConverter FILE_CONVERTER = new FileConverter();
    @Override
    public File deserialize(SettingsItem<File> requester, String name, Section section) {
        return new File(section.getString(name));
    }

    @Override
    public void serialize(SettingsItem<File> requester, String name, Section section, File value) {
        section.set(value.getAbsolutePath(), name);
    }
}
