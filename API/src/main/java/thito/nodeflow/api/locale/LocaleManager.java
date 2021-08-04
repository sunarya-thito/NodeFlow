package thito.nodeflow.api.locale;

import thito.nodeflow.api.resource.ResourceFile;

import java.util.*;

public interface LocaleManager {
    I18nItem getItem(String name);

    void loadLocale(ResourceFile file);

    ResourceBundle asBundle();
}
