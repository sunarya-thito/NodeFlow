package thito.nodeflow.internal.ui.settings;

import thito.nodeflow.api.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.ui.layout.*;

public class SettingsUI extends UIComponent {
    public SettingsUI() {
        getChildren().add(((ApplicationSettingsImpl) NodeFlow.getApplication().getSettings()).getView());
    }
}
