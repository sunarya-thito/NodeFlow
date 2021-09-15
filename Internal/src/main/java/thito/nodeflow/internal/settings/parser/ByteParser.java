package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.internal.settings.*;

import java.util.*;

public class ByteParser implements SettingsParser<Byte> {
    @Override
    public Optional<Byte> fromConfig(Section source, String key) {
        return source.getByte(key);
    }

    @Override
    public void toConfig(Section source, String key, Byte value) {
        source.set(key, value);
    }
}
