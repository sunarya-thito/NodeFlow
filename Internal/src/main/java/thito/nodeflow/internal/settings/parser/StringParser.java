package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.internal.settings.*;

import java.util.*;

public class StringParser implements SettingsParser<String> {

    @Override
    public Optional<String> fromConfig(Section source, String key) {
        return source.getString(key);
    }

    @Override
    public void toConfig(Section source, String key, String value) {
        source.set(key, value);
    }
}
