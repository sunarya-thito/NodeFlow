package thito.nodeflow.internal.event.window;

import thito.nodeflow.api.event.window.*;
import thito.nodeflow.api.ui.*;

public class WindowOpenEventImpl extends WindowEventImpl implements WindowOpenEvent {
    public WindowOpenEventImpl(Window window) {
        super(window);
    }
}
