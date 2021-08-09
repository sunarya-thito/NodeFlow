package thito.nodeflow.internal.settings;

import com.dlsc.preferencesfx.model.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.task.*;

import java.util.*;

public class SettingsCategoryImpl implements SettingsCategory {
    private String name;
    private I18nItem display;
    private ObservableList<SettingsGroup> groups = FXCollections.observableArrayList();
    private ObservableList<SettingsCategory> children = FXCollections.observableArrayList();

    public SettingsCategoryImpl(String name, I18nItem display) {
        this.name = name;
        this.display = display;
        children.addListener((InvalidationListener)  obs -> {
            Task.runOnForeground("update-settings-category", () -> {
                ((ApplicationSettingsImpl) NodeFlow.getApplication().getSettings()).updatePreferencesFX();
            });
        });
        groups.addListener((InvalidationListener)  obs -> {
            Task.runOnForeground("update-settings-group", () -> {
                ((ApplicationSettingsImpl) NodeFlow.getApplication().getSettings()).updatePreferencesFX();
            });
        });
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public I18nItem getDisplayName() {
        return display;
    }

    @Override
    public List<SettingsGroup> getGroups() {
        return groups;
    }

    @Override
    public List<SettingsCategory> getChildren() {
        return children;
    }

    public Category toCategory() {
        Category cx = Category.of(getDisplayName().name(), getGroups().stream().map(group ->
                Group.of(group.getDisplay().name(), group.getItems().stream().map(item -> ((AbstractSettingsItem) item).createSetting()).toArray(Setting[]::new))).toArray(Group[]::new)).subCategories(getChildren().stream().map(category -> ((SettingsCategoryImpl) category).toCategory()).toArray(Category[]::new)
        );
        return cx;
    }
}
