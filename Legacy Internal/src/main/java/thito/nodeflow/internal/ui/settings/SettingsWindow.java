package thito.nodeflow.internal.ui.settings;

import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.ui.*;

public class SettingsWindow extends WindowImpl {

    public SettingsWindow() {
        getStage().titleProperty().bind(I18n.$("settings-window-title").stringBinding());
        getMenu().getItems().add(requestDefaultApplicationMenu());
        getMenu().getItems().add(requestDefaultWindowMenu());
        getMenu().getItems().add(requestDefaultHelpMenu());
    }

    @Override
    protected void initializeViewport() {
        setViewport(new SettingsUI());
    }

    @Override
    public String getName() {
        return "Settings";
    }
}
