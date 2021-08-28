package thito.nodeflow.internal.ui.settings;

import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.language.*;

public class SettingsWindow extends StandardWindow {

    private static SettingsWindow opened;
    public static SettingsWindow open() {
        if (opened != null) {
            opened.getStage().toFront();
            return opened;
        }
        opened = new SettingsWindow();
        opened.show();
        return opened;
    }

    public SettingsWindow() {
        titleProperty().bind(I18n.$("settings.title"));
    }

    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        getStage().showingProperty().addListener((obs, old, val) -> {
            if (!val && opened == this) opened = null;
        });
        contentProperty().set(new SettingsSkin());
    }

}
