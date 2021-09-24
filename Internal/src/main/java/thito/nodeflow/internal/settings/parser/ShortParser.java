package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.internal.settings.*;

import java.util.*;

public class ShortParser implements SettingsParser<Short> {
    @Override
    public Optional<Short> fromConfig(Section source, String key) {
        return source.getShort(key);
    }

    @Override
    public void toConfig(Section source, String key, Short value) {
        source.set(key, value);
    }
}
