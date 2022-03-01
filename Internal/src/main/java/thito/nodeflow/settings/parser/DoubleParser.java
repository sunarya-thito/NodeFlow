package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class DoubleParser implements SettingsParser<Double> {
    @Override
    public Optional<Double> fromConfig(Section source, String key) {
        return source.getDouble(new Path(key));
    }

    @Override
    public void toConfig(Section source, String key, Double value) {
        source.set(new Path(key), value);
    }
}
