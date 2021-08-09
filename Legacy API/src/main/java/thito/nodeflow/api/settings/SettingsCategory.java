package thito.nodeflow.api.settings;

import thito.nodeflow.api.locale.*;

import java.util.*;

public interface SettingsCategory {
    String name();
    I18nItem getDisplayName();
    List<SettingsGroup> getGroups();
    List<SettingsCategory> getChildren();
    default SettingsCategory addChildren(SettingsCategory... settingsCategories) {
        for (SettingsCategory category : settingsCategories) {
            getChildren().add(category);
        }
        return this;
    }
    default SettingsCategory addGroup(SettingsGroup... groups) {
        for (SettingsGroup group : groups) {
            getGroups().add(group);
        }
        return this;
    }
}
