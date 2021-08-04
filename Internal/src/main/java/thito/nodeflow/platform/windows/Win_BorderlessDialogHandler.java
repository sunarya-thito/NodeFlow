package thito.nodeflow.platform.windows;

import com.sun.jna.platform.win32.*;
import thito.nodeflow.platform.*;

@Deprecated
/*
No need to create native implementation for this one,
just use javafx features for transparent stage style
 */
public class Win_BorderlessDialogHandler implements NativeHandle, WinUser.WindowProc {

    @Override
    public WinDef.LRESULT callback(WinDef.HWND hWnd, int i, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
        return null;
    }

}
