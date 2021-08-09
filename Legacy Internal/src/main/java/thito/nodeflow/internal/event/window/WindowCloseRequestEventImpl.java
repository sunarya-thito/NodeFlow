package thito.nodeflow.internal.event.window;

import thito.nodeflow.api.event.window.*;
import thito.nodeflow.api.ui.*;

public class WindowCloseRequestEventImpl extends WindowEventImpl implements WindowCloseRequestEvent {
    public WindowCloseRequestEventImpl(Window window) {
        super(window);
    }
}
