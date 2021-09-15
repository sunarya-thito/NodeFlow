package thito.nodeflow.internal.settings.parser;

import thito.nodeflow.internal.settings.*;
import thito.nodeflow.config.*;
import thito.nodeflow.library.language.*;

import java.util.*;

public class LanguageParser implements SettingsParser<Language> {
    @Override
    public Optional<Language> fromConfig(Section source, String key) {
        return source.getString(key).map(Language::new);
    }

    @Override
    public void toConfig(Section source, String key, Language value) {
        source.set(key, value.getCode());
    }
}
