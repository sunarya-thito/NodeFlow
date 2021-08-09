package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.stage.*;
import thito.nodeflow.internal.platform.*;
import thito.nodeflow.internal.ui.skin.*;

public class Window {
    protected Stage stage = new Stage();
    private WindowTitleBarInfo titleBarInfo = new WindowTitleBarInfo();
    private ObjectProperty<Skin> skin = new SimpleObjectProperty<>();

    public Window() {
        initializeWindow();
    }

    protected void initializeWindow() {
        NativeToolkit.TOOLKIT.makeBorderless(this);
    }

    public Stage getStage() {
        return stage;
    }

    public WindowTitleBarInfo getTitleBarInfo() {
        return titleBarInfo;
    }
}
