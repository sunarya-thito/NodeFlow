package thito.nodeflow.platform.windows;

import com.sun.jna.platform.win32.*;
import javafx.scene.*;
import javafx.stage.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.ui.decoration.window.*;
import thito.nodeflow.platform.*;

import java.lang.reflect.*;

import static com.sun.jna.platform.win32.WinUser.*;

public class Win_BorderlessWindowHandler implements NativeHandle, WinUser.WindowProc {

    private static final int WM_NCCCALCSIZE = 0x0083;
    private static final int WM_NCHITTEST = 0x0084;
    private static final int WM_NCRBUTTONDOWN = 0x00A4;

    private final WindowBar bar;

    private int
            maximizedWindowFrameThickness = 10,
            frameResizeBorderThickness = 4
                    ;

    private BaseTSD.LONG_PTR defWndProc;
    public Win_BorderlessWindowHandler(WindowBar bar) {
        this.bar = bar;
        bar.getBase().getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            init();
            update();
        });
    }

    private void update() {
        Scene scene = bar.getBase().getStage().getScene();
        if (scene != null) {
                /*
                Patch Fix
                Due to window border change. Scene needs to be repositioned.
                 */
            try {
                Method method = Scene.class.getDeclaredMethod("setY", double.class);
                method.setAccessible(true);
                method.invoke(scene, 0);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            try {
                Method method = Scene.class.getDeclaredMethod("setX", double.class);
                method.setAccessible(true);
                method.invoke(scene, 0);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void init() {
        WinDef.HWND hWnd = new WinDef.HWND(Toolkit.getWindowPointer(bar.getBase().getStage()));
        defWndProc = Win_User32.INSTANCE.SetWindowLongPtr(hWnd, Win_User32.GWLP_WNDPROC, this);
        User32.INSTANCE.SetWindowPos(hWnd, hWnd, 0, 0, 0, 0,
                WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOZORDER | WinUser.SWP_FRAMECHANGED);
        User32 user32 = User32.INSTANCE;
        BaseTSD.LONG_PTR ws = user32.GetWindowLongPtr(hWnd, User32.GWL_STYLE);
        ws.setValue(ws.longValue() | User32.WS_SIZEBOX | User32.WS_CAPTION);
        user32.SetWindowLongPtr(hWnd, User32.GWL_STYLE, ws.toPointer());
    }

    @Override
    public WinDef.LRESULT callback(WinDef.HWND hWnd, int i, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
        switch (i) {
            case WM_NCCCALCSIZE:
                return new WinDef.LRESULT(0);
            case WM_NCHITTEST:
                WinDef.LRESULT lResult = handle$WM_NCHITTEST(hWnd, i, wParam, lParam);
                if (lResult.intValue() == new WinDef.LRESULT(0).intValue()) {
                    return Win_User32.INSTANCE.CallWindowProc(defWndProc, hWnd, i, wParam, lParam);
                }
                return lResult;
            case WM_DESTROY:
                Win_User32.INSTANCE.SetWindowLongPtr(hWnd, Win_User32.GWLP_WNDPROC, defWndProc);
                return new WinDef.LRESULT(0);
            case WM_NCRBUTTONDOWN:
                WinDef.POINT ptMouse = new WinDef.POINT();
                User32.INSTANCE.GetCursorPos(ptMouse);
                bar.showContextMenu(ptMouse.x, ptMouse.y);
                return new WinDef.LRESULT(0);
            default:
                return Win_User32.INSTANCE.CallWindowProc(defWndProc, hWnd, i, wParam, lParam);
        }
    }

    private WinDef.LRESULT handle$WM_NCHITTEST(WinDef.HWND hWnd, int message, WinDef.WPARAM wPara, WinDef.LPARAM lParam) {
        int borderOffset = maximizedWindowFrameThickness;
        int borderThickness = frameResizeBorderThickness;

        WinDef.POINT ptMouse = new WinDef.POINT();
        WinDef.RECT rcWindow = new WinDef.RECT();
        User32.INSTANCE.GetCursorPos(ptMouse);
        User32.INSTANCE.GetWindowRect(hWnd, rcWindow);

        int uRow = 1, uCol = 1;
        boolean fOnResizeBorder = false, fOnFrameDrag = false;

        int titleBarHeight = bar.getPane().heightProperty().intValue();
        int topPadding = StaticMenuBar.HEIGHT;
        // not actually bottom padding, its the height
        int bottomPadding = titleBarHeight <= 0 ? borderThickness : titleBarHeight;
        if (bar.getBase().getStage().isMaximized()) {
            bottomPadding += 8;
        }
        int leftPadding = 10;
        int rightPadding = 10;

        rcWindow.top += topPadding;
        if (ptMouse.y >= rcWindow.top && ptMouse.y < rcWindow.top + bottomPadding) {
            fOnResizeBorder = (ptMouse.y < (rcWindow.top + borderThickness));  // Top Resizing
            if (!fOnResizeBorder) {
                fOnFrameDrag = (ptMouse.y <= rcWindow.top + titleBarHeight+ borderOffset)
                        && (ptMouse.x < (rcWindow.right - (rightPadding + borderOffset)))
                        && (ptMouse.x > (rcWindow.left + leftPadding + borderOffset))
                        && !bar.getControl().preventWindowDrag(ptMouse.x, ptMouse.y);
            }
            uRow = 0; // Top Resizing or Caption Moving
        } else if (ptMouse.y < rcWindow.bottom && ptMouse.y >= rcWindow.bottom - borderThickness)
            uRow = 2; // Bottom Resizing
        if (ptMouse.x >= rcWindow.left && ptMouse.x < rcWindow.left + borderThickness)
            uCol = 0; // Left Resizing
        else if (ptMouse.x < rcWindow.right && ptMouse.x >= rcWindow.right - borderThickness)
            uCol = 2; // Right Resizing

        final int HTTOPLEFT = 13, HTTOP = 12, HTCAPTION = 2, HTTOPRIGHT = 14, HTLEFT = 10, HTNOWHERE = 0,
                HTRIGHT = 11, HTBOTTOMLEFT = 16, HTBOTTOM = 15, HTBOTTOMRIGHT = 17, HTSYSMENU = 3;

        int[][] hitTests = {
                {HTTOPLEFT, fOnResizeBorder ? HTTOP : fOnFrameDrag ? HTCAPTION : HTNOWHERE, HTTOPRIGHT},
                {HTLEFT, HTNOWHERE, HTRIGHT},
                {HTBOTTOMLEFT, HTBOTTOM, HTBOTTOMRIGHT},
        };

        return new WinDef.LRESULT(hitTests[uRow][uCol]);
    }
}
