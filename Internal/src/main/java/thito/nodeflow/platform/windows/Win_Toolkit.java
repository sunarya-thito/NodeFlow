package thito.nodeflow.platform.windows;

import com.sun.jna.platform.win32.*;
import javafx.stage.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.platform.*;

public class Win_Toolkit implements NativeToolkit {
    @Override
    public void makeFullyTransparent(Stage stage) {
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            WinDef.HWND hWnd = new WinDef.HWND(Toolkit.getWindowPointer(stage));
            User32 user32 = User32.INSTANCE;
            int ws = user32.GetWindowLong(hWnd, User32.GWL_EXSTYLE);
            ws |= User32.WS_EX_TRANSPARENT | User32.WS_EX_LAYERED | User32.WS_EX_TOPMOST | User32.WS_EX_COMPOSITED;
            user32.SetWindowLong(hWnd, User32.GWL_EXSTYLE, ws);
        });
    }

}
