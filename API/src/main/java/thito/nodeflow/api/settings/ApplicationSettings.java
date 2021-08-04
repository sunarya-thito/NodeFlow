package thito.nodeflow.api.settings;

import thito.nodeflow.api.locale.*;

import java.util.List;

public interface ApplicationSettings {
    String
    ASK_BEFORE_EXIT = "ask-before-exit",
    AUTOSAVE_INTERVAL = "autosave-interval",
    WORKSPACE_DIRECTORY = "workspace-directory",
    BUNDLES_DIRECTORY = "bundles-directory",
//    SHOW_INTRO = "show-intro",
    LANGUAGE = "language",
    THEME = "theme"
    ;
    List<SettingsCategory> getCategories();
    default SettingsGroup createGroup(I18nItem displayName, SettingsItem<?>... items) {
        return createGroup(null, displayName, items);
    }
    default SettingsCategory createCategory(I18nItem displayName, SettingsGroup... groups) {
        return createCategory(null, displayName, groups);
    }
    SettingsGroup createGroup(String name, I18nItem displayName, SettingsItem<?>... items);
    SettingsCategory createCategory(String name, I18nItem displayName, SettingsGroup... groups);
    default <T> SettingsItem<T> get(String name) {
        return lookup(getCategories(), name);
    }
    default <T> T getValue(String name) {
        return this.<T>get(name).getValue();
    }
    static <T> SettingsItem<T> lookup(List<SettingsCategory> categories, String name) {
        for (int i = 0; i < categories.size(); i++) {
            SettingsCategory category = categories.get(i);
            SettingsItem<T> item = lookup(category, name);
            if (item != null) {
                return item;
            }
        }
        return null;
    }
    static <T> SettingsItem<T> lookup(SettingsCategory category, String name) {
        for (int i = 0; i < category.getGroups().size(); i++) {
            SettingsItem<T> item = lookup(category.getGroups().get(i), name);
            if (item != null) {
                return item;
            }
        }
        return lookup(category.getChildren(), name);
    }
    static <T> SettingsItem<T> lookup(SettingsGroup group, String name) {
        for (int i = 0; i < group.getItems().size(); i++) {
            SettingsItem item = group.getItems().get(i);
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

}
