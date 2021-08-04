package thito.nodeflow.library.ui;

import javafx.animation.*;

public interface ViewportTransition {
    void updatePosition(ViewportPane viewportPane);
    Timeline play(ViewportPane viewportPane, Runnable onDone);
}
