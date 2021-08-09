package thito.nodeflow.api.event.window;

import thito.nodeflow.api.event.Event;
import thito.nodeflow.api.ui.Window;

public interface WindowEvent extends Event {
    Window getWindow();
}
