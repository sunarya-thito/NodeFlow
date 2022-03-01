package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class BooleanParser implements SettingsParser<Boolean> {
    @Override
    public Optional<Boolean> fromConfig(Section source, String key) {
        return source.getBoolean(new Path(key));
    }

    @Override
    public void toConfig(Section source, String key, Boolean value) {
        source.set(new Path(key), value);
    }
}
