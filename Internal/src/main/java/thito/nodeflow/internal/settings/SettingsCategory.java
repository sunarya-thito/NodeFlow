package thito.nodeflow.internal.settings;

import javafx.collections.*;

public class SettingsCategory {
    private SettingsDescription description;
    private Class<? extends Settings> type;
    private Settings settings;
    SettingsCategory parent;
    ObservableList<SettingsProperty<?>> settingsPropertyList = FXCollections.observableArrayList();
    ObservableList<SettingsCategory> subCategory = FXCollections.observableArrayList();

    public SettingsCategory(Class<? extends Settings> type, Settings settings) {
        this.settings = settings;
        this.description = settings.description();
    }

    public Settings getSettings() {
        return settings;
    }

    public Class<? extends Settings> getType() {
        return type;
    }

    public SettingsCategory getParent() {
        return parent;
    }

    public SettingsDescription getDescription() {
        return description;
    }

    public ObservableList<SettingsCategory> getSubCategory() {
        return subCategory;
    }

    public ObservableList<SettingsProperty<?>> getSettingsPropertyList() {
        return settingsPropertyList;
    }
}
