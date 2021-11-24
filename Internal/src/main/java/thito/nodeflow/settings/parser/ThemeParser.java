package thito.nodeflow.settings.parser;

import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;
import thito.nodeflow.ui.Theme;

import java.util.*;

public class ThemeParser implements SettingsParser<Theme> {
    @Override
    public Optional<Theme> fromConfig(Section source, String key) {
        return source.getString(key).map(Theme::new);
    }

    @Override
    public void toConfig(Section source, String key, Theme value) {
        source.set(key, value.getName());
    }
}
