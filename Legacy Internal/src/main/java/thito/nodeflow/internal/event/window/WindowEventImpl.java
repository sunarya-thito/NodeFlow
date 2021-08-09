package thito.nodeflow.internal.event.window;

import thito.nodeflow.api.event.window.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.event.*;

public class WindowEventImpl extends AbstractEvent implements WindowEvent {
    private final Window window;

    public WindowEventImpl(Window window) {
        this.window = window;
    }

    @Override
    public Window getWindow() {
        return window;
    }
}
