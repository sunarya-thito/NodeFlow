package thito.nodeflow.library.ui;

/**
 * https://docs.microsoft.com/en-us/windows/win32/inputdev/wm-nchittest
 */
public enum HitTestAction {
    ERROR(-2),
    // unknown
    TRANSPARENT(-1),
    /**
     * Does nothing to the window
     */
    NOWHERE(0),
    /**
     * Does nothing to the window
     */
    CLIENT(1),
    /**
     * Allow users to drag the window
     */
    CAPTION(2),
    SYSTEM_MENU(3),
    SIZE(4),
    GROWBOX(4),
    MENU(5),
    HORIZONTAL_SCROLL_BAR(6),
    VERTICAL_SCROLL_BAR(7),
    MINIMIZE_BUTTON(8),
    REDUCE(8),
    MAXIMIZE_BUTTON(9),
    ZOOM(9),
    LEFT(10),
    RIGHT(11),
    TOP(12),
    TOP_LEFT(13),
    TOP_RIGHT(14),
    /**
     * Allow users to resize vertically the window using the bottom border
     */
    BOTTOM(15),
    /**
     * Allow users to resize diagonally the window using the bottom left border
     */
    BOTTOM_LEFT(16),
    /**
     * Allow users to resize diagonally the window using the bottom right border
     */
    BOTTOM_RIGHT(17),
    // unknown
    BORDER(18),
    // theres no 19 wtf
    CLOSE_BUTTON(20),
    HELP_BUTTON(21);
    int value;
    HitTestAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
