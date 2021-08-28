package thito.nodeflow.library.platform.windows;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.*;

public interface Win_User32 extends User32 {
    Win_User32 INSTANCE = Native.load("user32", Win_User32.class, W32APIOptions.DEFAULT_OPTIONS);
    int GWLP_WNDPROC = -4;
    int WM_NCCCALCSIZE = 0x0083;
    int WM_NCHITTEST = 0x0084;
    int WM_LBUTTONDOWN = 0x0201;
    int WM_LBUTTONUP = 0x0202;
    int WM_RBUTTONDOWN = 0x0204;
    int WM_RBUTTONUP = 0x0205;
    int WM_MBUTTONDOWN = 0x0207;
    int WM_MBUTTONUP = 0x0208;
    int WM_NCLBUTTONDOWN = 0x00A1;
    int WM_NCLBUTTONUP = 0x00A2;
    int WM_NCMBUTTONDOWN = 0x00A7;
    int WM_NCMBUTTONUP = 0x00A8;
    int WM_NCRBUTTONDOWN = 0x00A4;
    int WM_NCRBUTTONUP = 0x00A5;

    BaseTSD.LONG_PTR SetWindowLongPtr(HWND var1, int var2, WindowProc var3);

    LONG_PTR SetWindowLongPtr(HWND var1, int var2, LONG_PTR var3);

    LRESULT CallWindowProc(LONG_PTR var1, HWND var2, int var3, WPARAM var4, LPARAM var5);
}