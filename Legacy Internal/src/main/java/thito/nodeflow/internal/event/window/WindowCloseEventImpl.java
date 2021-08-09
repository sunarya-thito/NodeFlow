package thito.nodeflow.internal.event.window;

import thito.nodeflow.api.event.window.*;
import thito.nodeflow.api.ui.*;

public class WindowCloseEventImpl extends WindowEventImpl implements WindowCloseEvent {
    public WindowCloseEventImpl(Window window) {
        super(window);
    }
}
