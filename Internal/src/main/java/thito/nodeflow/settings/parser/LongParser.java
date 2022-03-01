package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class LongParser implements SettingsParser<Long> {
    @Override
    public Optional<Long> fromConfig(Section source, String key) {
        return source.getLong(new Path(key));
    }

    @Override
    public void toConfig(Section source, String key, Long value) {
        source.set(new Path(key), value);
    }
}
