package thito.nodeflow.internal.settings;

import java.util.*;

public class SettingsCategory {
    private SettingsDescription description;
    List<SettingsProperty<?>> settingsPropertyList = new ArrayList<>();
    List<SettingsCategory> subCategory = new ArrayList<>();

    public SettingsCategory(SettingsDescription description) {
        this.description = description;
    }

    public SettingsDescription getDescription() {
        return description;
    }

    public List<SettingsCategory> getSubCategory() {
        return Collections.unmodifiableList(subCategory);
    }

    public List<SettingsProperty<?>> getSettingsPropertyList() {
        return Collections.unmodifiableList(settingsPropertyList);
    }
}
