package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.internal.settings.*;

import java.io.*;
import java.util.*;

public class FileParser implements SettingsParser<File> {
    @Override
    public Optional<File> fromConfig(Section source, String key) {
        return source.getString(key).map(File::new);
    }

    @Override
    public void toConfig(Section source, String key, File value) {
        source.set(key, value.getAbsolutePath());
    }
}
