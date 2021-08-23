package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.node.*;
import thito.nodeflow.library.config.*;

import java.util.*;

public class StringParser implements SettingsParser<String> {

    public static class Factory implements SettingsNodeFactory<String> {
        @Override
        public SettingsNode<String> createNode(SettingsProperty<String> item) {
            return new StringNode(item);
        }
    }
    @Override
    public Optional<String> fromConfig(Section source, String key) {
        return source.getString(key);
    }

    @Override
    public void toConfig(Section source, String key, String value) {
        source.set(key, value);
    }
}
