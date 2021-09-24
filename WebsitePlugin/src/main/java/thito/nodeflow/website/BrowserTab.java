package thito.nodeflow.website;

import javafx.scene.control.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.ui.editor.*;

public class BrowserTab extends EditorTab {

    private Tab tab;

    // Layout

    // End Layout

    public BrowserTab() {
        tab = new Tab();
        tab.textProperty().bind(I18n.$("plugin.browser"));
    }

    @Override
    public Tab getTab() {
        return null;
    }
}
