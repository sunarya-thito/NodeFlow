package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class StringParser implements SettingsParser<String> {

    @Override
    public Optional<String> fromConfig(Section source, String key) {
        return source.getString(new Path(key));
    }

    @Override
    public void toConfig(Section source, String key, String value) {
        source.set(new Path(key), value);
    }
}
