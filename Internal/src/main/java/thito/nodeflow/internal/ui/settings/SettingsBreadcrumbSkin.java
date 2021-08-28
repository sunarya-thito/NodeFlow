package thito.nodeflow.internal.ui.settings;

import javafx.scene.control.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.Skin;

public class SettingsBreadcrumbSkin extends Skin {
    @Component("breadcrumb-label")
    Label label;

    private I18n text;

    public SettingsBreadcrumbSkin(I18n text) {
        this.text = text;
    }

    @Override
    protected void onLayoutLoaded() {
        label.textProperty().bind(text);
    }
}
