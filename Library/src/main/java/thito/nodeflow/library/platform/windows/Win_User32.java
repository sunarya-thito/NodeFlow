package thito.nodeflow.library.platform.windows;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.*;

public interface Win_User32 extends User32 {
    Win_User32 INSTANCE = Native.load("user32", Win_User32.class, W32APIOptions.DEFAULT_OPTIONS);
    int GWLP_WNDPROC = -4;
    int WM_NCCCALCSIZE = 0x0083;
    int WM_NCHITTEST = 0x0084;

    BaseTSD.LONG_PTR SetWindowLongPtr(HWND var1, int var2, WindowProc var3);

    LONG_PTR SetWindowLongPtr(HWND var1, int var2, LONG_PTR var3);

    LRESULT CallWindowProc(LONG_PTR var1, HWND var2, int var3, WPARAM var4, LPARAM var5);
}