package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.config.*;
import thito.nodeflow.library.ui.*;

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
