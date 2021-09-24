package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.internal.settings.*;

import java.util.*;

public class DoubleParser implements SettingsParser<Double> {
    @Override
    public Optional<Double> fromConfig(Section source, String key) {
        return source.getDouble(key);
    }

    @Override
    public void toConfig(Section source, String key, Double value) {
        source.set(key, value);
    }
}
