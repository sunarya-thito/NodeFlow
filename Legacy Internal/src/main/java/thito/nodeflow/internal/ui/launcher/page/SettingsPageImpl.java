package thito.nodeflow.internal.ui.launcher.page;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.launcher.page.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.ui.launcher.*;

public class SettingsPageImpl extends AbstractLauncherPage implements SettingsPage {
    private BooleanProperty changed = new SimpleBooleanProperty();
    public SettingsPageImpl() {
        super(I18n.$("launcher-button-settings"), I18n.$("launcher-page-settings"), null,
                NodeFlow.getApplication().getResourceManager().getIcon("launcher-button-settings"));
        setHeaderEnabled(false);
    }

    public BooleanProperty changedProperty() {
        return changed;
    }

    @Override
    protected Node requestViewport() {
        StackPane pane = ((ApplicationSettingsImpl) NodeFlow.getApplication().getSettings()).getView();
        Toolkit.style(pane, "settings-frame");
        return pane;
    }

    public StackPane getFootButtons() {
        return impl_getPeer().getFootButtons();
    }
}
