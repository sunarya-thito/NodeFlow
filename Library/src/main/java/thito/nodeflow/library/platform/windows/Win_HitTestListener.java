package thito.nodeflow.library.platform.windows;

import com.sun.javafx.stage.*;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import javafx.scene.input.*;
import javafx.stage.*;
import thito.nodeflow.library.ui.Window;

import static com.sun.jna.platform.win32.WinUser.*;

public class Win_HitTestListener implements WinUser.WindowProc {

    private final Window window;

    private BaseTSD.LONG_PTR defaultWindowHandler;

    public Win_HitTestListener(Window bar) {
        this.window = bar;
        bar.getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            init();
        });
    }

    private void init() {
        Win_User32 user32 = Win_User32.INSTANCE;

        WinDef.HWND hWnd = new WinDef.HWND(new Pointer(StageHelper.getPeer(window.getStage()).getRawHandle()));

        defaultWindowHandler = Win_User32.INSTANCE.SetWindowLongPtr(hWnd, Win_User32.GWLP_WNDPROC, this);

        user32.SetWindowPos(hWnd, hWnd, 0, 0, 0, 0,
                WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOZORDER | WinUser.SWP_FRAMECHANGED);

        int style = user32.GetWindowLong(hWnd, GWL_STYLE);
        style |= User32.WS_SIZEBOX | User32.WS_CAPTION;
        user32.SetWindowLong(hWnd, GWL_STYLE, style);

//        int extendedStyle = user32.GetWindowLong(hWnd, User32.GWL_EXSTYLE);
//        extendedStyle |= User32.WS_EX_LAYERED | User32.WS_EX_TOPMOST | WS_EX_COMPOSITED | WS_EX_TRANSPARENT;
//        user32.SetWindowLong(hWnd, User32.GWL_EXSTYLE, extendedStyle);
    }

    @Override
    public WinDef.LRESULT callback(WinDef.HWND hWnd, int i, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
        switch (i) {
            case Win_User32.WM_NCCCALCSIZE:
                return new WinDef.LRESULT(0);
            case Win_User32.WM_NCHITTEST:
                return handle$WM_NCHITTEST(hWnd, i, wParam, lParam);
            case WM_DESTROY:
                Win_User32.INSTANCE.SetWindowLongPtr(hWnd, Win_User32.GWLP_WNDPROC, defaultWindowHandler);
                return new WinDef.LRESULT(0);
            case Win_User32.WM_NCLBUTTONDOWN:
            case Win_User32.WM_LBUTTONDOWN:
                MOUSE_BUTTON = MouseButton.PRIMARY;
                return Win_User32.INSTANCE.CallWindowProc(defaultWindowHandler, hWnd, i, wParam, lParam);
            case Win_User32.WM_NCRBUTTONDOWN:
            case Win_User32.WM_RBUTTONDOWN:
                MOUSE_BUTTON = MouseButton.SECONDARY;
                return Win_User32.INSTANCE.CallWindowProc(defaultWindowHandler, hWnd, i, wParam, lParam);
            case Win_User32.WM_NCMBUTTONDOWN:
            case Win_User32.WM_MBUTTONDOWN:
                MOUSE_BUTTON = MouseButton.MIDDLE;
                return Win_User32.INSTANCE.CallWindowProc(defaultWindowHandler, hWnd, i, wParam, lParam);
            case Win_User32.WM_LBUTTONUP:
            case Win_User32.WM_MBUTTONUP:
            case Win_User32.WM_RBUTTONUP:
            case Win_User32.WM_NCLBUTTONUP:
            case Win_User32.WM_NCMBUTTONUP:
            case Win_User32.WM_NCRBUTTONUP:
                MOUSE_BUTTON = MouseButton.NONE;
                return Win_User32.INSTANCE.CallWindowProc(defaultWindowHandler, hWnd, i, wParam, lParam);
            default:
                return Win_User32.INSTANCE.CallWindowProc(defaultWindowHandler, hWnd, i, wParam, lParam);
        }
    }

    private MouseButton MOUSE_BUTTON = MouseButton.NONE;
    private WinDef.LRESULT handle$WM_NCHITTEST(WinDef.HWND hWnd, int message, WinDef.WPARAM wPara, WinDef.LPARAM lParam) {
        int dword = lParam.intValue();
        int mouseX = (short) (dword);
        int mouseY = (short) (dword >> 16);
        int result = window.getWindowHitTest().testHit(mouseX, mouseY, MOUSE_BUTTON).getValue();

        return new LRESULT(result);
    }
}