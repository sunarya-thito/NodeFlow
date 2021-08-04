package thito.nodeflow.platform.windows;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.*;

public interface Win_User32 extends User32 {
    Win_User32 INSTANCE = Native.load("user32", Win_User32.class, W32APIOptions.DEFAULT_OPTIONS);
    int GWLP_WNDPROC = -4;
    LONG_PTR SetWindowLongPtr(HWND hWnd, int nIndex, WindowProc wndProc);
    LONG_PTR SetWindowLongPtr(HWND hWnd, int nIndex, LONG_PTR wndProc);
    LRESULT CallWindowProc(LONG_PTR proc, HWND hWnd, int uMsg, WPARAM uParam, LPARAM lParam);
}
