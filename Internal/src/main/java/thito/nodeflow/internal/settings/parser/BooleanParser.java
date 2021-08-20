package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.config.*;

import java.util.*;

public class BooleanParser implements SettingsParser<Boolean> {
    @Override
    public Optional<Boolean> fromConfig(Section source, String key) {
        return source.getBoolean(key);
    }

    @Override
    public void toConfig(Section source, String key, Boolean value) {
        source.set(key, value);
    }
}
