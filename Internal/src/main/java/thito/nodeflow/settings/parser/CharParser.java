package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class CharParser implements SettingsParser<Character> {
    @Override
    public Optional<Character> fromConfig(Section source, String key) {
        return source.getCharacter(key);
    }

    @Override
    public void toConfig(Section source, String key, Character value) {
        source.set(key, value);
    }
}
