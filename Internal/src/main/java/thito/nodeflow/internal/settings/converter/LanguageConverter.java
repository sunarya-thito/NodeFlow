package thito.nodeflow.internal.settings.converter;

import thito.nodeflow.api.config.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.internal.locale.*;

public class LanguageConverter implements SettingsConverter<Language> {

    public static final LanguageConverter LANGUAGE_CONVERTER = new LanguageConverter();

    @Override
    public Language deserialize(SettingsItem<Language> requester, String name, Section section) {
        return new Language(section.getString(name));
    }

    @Override
    public void serialize(SettingsItem<Language> requester, String name, Section section, Language value) {
        section.set(value.getLocale().getLanguage(), name);
    }
}
