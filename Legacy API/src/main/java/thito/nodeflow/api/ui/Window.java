package thito.nodeflow.api.ui;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import thito.nodeflow.api.ui.menu.Menu;

public interface Window {
    Menu getMenu();

    void show();

    void hide();

    boolean isShowing();

    void attemptClose();

    void forceClose();

    boolean isFocused();

    void requestFocus();

    boolean isMaximized();

    void setMaximized(boolean maximized);

    boolean isMinimized();

    void setMinimized(boolean minimized);

    Stage impl_getPeer();

    Pane impl_getDialogLayer();

    Pane impl_getViewportLayer();

    String getName();
}
