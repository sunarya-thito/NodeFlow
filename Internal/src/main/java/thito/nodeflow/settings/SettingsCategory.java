package thito.nodeflow.settings;

import javafx.collections.*;
import thito.nodeflow.language.I18n;
import thito.nodeflow.settings.canvas.*;

public class SettingsCategory {
    private String key;
    private I18n displayName;
    private SettingsContext context;
    private ObservableList<SettingsItem<?>> items = FXCollections.observableArrayList();
    private ObservableList<SettingsCategory> subCategories = FXCollections.observableArrayList();

    public SettingsCategory(String key, I18n displayName, SettingsContext context) {
        this.key = key;
        this.displayName = displayName;
        this.context = context;
    }

    public SettingsContext getContext() {
        return context;
    }

    public String getKey() {
        return key;
    }

    public I18n getDisplayName() {
        return displayName;
    }

    public ObservableList<SettingsCategory> getSubCategories() {
        return subCategories;
    }

    public ObservableList<SettingsItem<?>> getItems() {
        return items;
    }
}
