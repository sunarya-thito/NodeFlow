package thito.nodeflow.internal.ui.settings;

import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.language.*;

public class SettingsWindow extends StandardWindow {
    public SettingsWindow() {
        titleProperty().bind(I18n.$("settings.title"));
    }

    @Override
    public void show() {
        contentProperty().set(new SettingsSkin());
        super.show();
    }
}
