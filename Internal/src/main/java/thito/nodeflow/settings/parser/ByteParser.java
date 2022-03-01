package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class ByteParser implements SettingsParser<Byte> {
    @Override
    public Optional<Byte> fromConfig(Section source, String key) {
        return source.getByte(new Path(key));
    }

    @Override
    public void toConfig(Section source, String key, Byte value) {
        source.set(new Path(key), value);
    }
}
