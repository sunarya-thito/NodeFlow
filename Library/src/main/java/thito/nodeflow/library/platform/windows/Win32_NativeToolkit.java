package thito.nodeflow.library.platform.windows;

import javafx.stage.*;
import thito.nodeflow.library.platform.*;
import thito.nodeflow.library.ui.Window;

public class Win32_NativeToolkit implements NativeToolkit {
    @Override
    public void registerNativeWindowHandling(Window window) {
        Stage stage = window.getStage();
        stage.initStyle(StageStyle.TRANSPARENT);
        new Win_HitTestListener(window);
    }
}
