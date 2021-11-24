package thito.nodeflow.settings;

import thito.nodeflow.config.*;

import java.util.*;

public interface SettingsParser<T> {
    Optional<T> fromConfig(Section source, String key);
    void toConfig(Section source, String key, T value);
}
