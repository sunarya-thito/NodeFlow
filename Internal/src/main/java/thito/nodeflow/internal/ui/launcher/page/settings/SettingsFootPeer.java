package thito.nodeflow.internal.ui.launcher.page.settings;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import thito.nodeflow.library.ui.layout.*;

@Deprecated
public class SettingsFootPeer extends UIComponent {

    @Component("reset")
    private final ObjectProperty<JFXButton> reset = new SimpleObjectProperty<>();

    @Component("apply")
    private final ObjectProperty<JFXButton> apply = new SimpleObjectProperty<>();

    public SettingsFootPeer() {
        setLayout(Layout.loadLayout("SettingsFootButtonsUI"));
    }

    public JFXButton getReset() {
        return reset.get();
    }

    public JFXButton getApply() {
        return apply.get();
    }
}
