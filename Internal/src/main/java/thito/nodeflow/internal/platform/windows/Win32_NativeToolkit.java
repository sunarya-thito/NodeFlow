package thito.nodeflow.internal.platform.windows;

import javafx.stage.*;
import thito.nodeflow.internal.platform.*;
import thito.nodeflow.internal.ui.Window;

public class Win32_NativeToolkit implements NativeToolkit {
    @Override
    public void registerNativeWindowHandling(Window window) {
        Stage stage = window.getStage();
        stage.initStyle(StageStyle.TRANSPARENT);
        new Win_HitTestListener(window);
    }
}
