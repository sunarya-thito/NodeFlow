package thito.nodeflow.platform.windows;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.*;

@Deprecated
public interface Win_DesktopWindowManager extends StdCallLibrary {
    Win_DesktopWindowManager INSTANCE = Native.load("Dwmapi", Win_DesktopWindowManager.class, W32APIOptions.DEFAULT_OPTIONS);
    WinNT.HRESULT DwmExtendFrameIntoClientArea(WinDef.HWND hWnd, MARGINS margins);
    WinNT.HRESULT DwmIsCompositionEnabled(WinDef.BOOL pfEnabled);
    WinNT.HRESULT DwmEnableComposition(
            WinDef.UINT uCompositionAction
    );

    @Structure.FieldOrder({"cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight"})
    class MARGINS extends Structure {
        public int cxLeftWidth;
        public int cxRightWidth;
        public int cyTopHeight;
        public int cyBottomHeight;

        public MARGINS(int cxLeftWidth, int cxRightWidth, int cyTopHeight, int cyBottomHeight) {
            this.cxLeftWidth = cxLeftWidth;
            this.cxRightWidth = cxRightWidth;
            this.cyTopHeight = cyTopHeight;
            this.cyBottomHeight = cyBottomHeight;
        }
    }
}
