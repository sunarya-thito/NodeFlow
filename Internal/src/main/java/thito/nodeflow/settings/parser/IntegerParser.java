package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class IntegerParser implements SettingsParser<Integer> {
    @Override
    public Optional<Integer> fromConfig(Section source, String key) {
        return source.getInteger(key);
    }

    @Override
    public void toConfig(Section source, String key, Integer value) {
        source.set(key, value);
    }
}
