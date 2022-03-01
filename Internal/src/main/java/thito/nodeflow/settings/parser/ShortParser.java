package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class ShortParser implements SettingsParser<Short> {
    @Override
    public Optional<Short> fromConfig(Section source, String key) {
        return source.getShort(new Path(key));
    }

    @Override
    public void toConfig(Section source, String key, Short value) {
        source.set(new Path(key), value);
    }
}
