package thito.nodeflow.library.platform.unknown;

import javafx.stage.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.platform.*;
import thito.nodeflow.library.ui.Window;

public class Unknown_NativeToolkit implements NativeToolkit {
    @Override
    public void registerNativeWindowHandling(Window window) {
        Stage stage = window.getStage();
        stage.initStyle(StageStyle.TRANSPARENT);
    }
}
