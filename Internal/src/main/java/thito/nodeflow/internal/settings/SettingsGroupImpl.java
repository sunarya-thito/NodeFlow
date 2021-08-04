package thito.nodeflow.internal.settings;

import javafx.beans.*;
import javafx.collections.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.settings.*;
import thito.nodeflow.api.task.*;

import java.util.*;

public class SettingsGroupImpl implements SettingsGroup {
    private String name;
    private I18nItem display;
    private ObservableList<SettingsItem<?>> items = FXCollections.observableArrayList();

    public SettingsGroupImpl(String name, I18nItem display) {
        this.name = name;
        this.display = display;
        items.addListener((InvalidationListener) obs -> {
            Task.runOnForeground("update-settings-items", () -> {
                ((ApplicationSettingsImpl) NodeFlow.getApplication().getSettings()).updatePreferencesFX();
            });
        });
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public I18nItem getDisplay() {
        return display;
    }

    @Override
    public List<SettingsItem<?>> getItems() {
        return items;
    }
}
