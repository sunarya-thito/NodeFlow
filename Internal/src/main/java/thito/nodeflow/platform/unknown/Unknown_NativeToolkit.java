package thito.nodeflow.platform.unknown;

import javafx.stage.*;
import thito.nodeflow.platform.NativeToolkit;
import thito.nodeflow.ui.Window;

public class Unknown_NativeToolkit implements NativeToolkit {
    @Override
    public void registerNativeWindowHandling(Window window) {
        Stage stage = window.getStage();
        stage.initStyle(StageStyle.TRANSPARENT);
    }
}
