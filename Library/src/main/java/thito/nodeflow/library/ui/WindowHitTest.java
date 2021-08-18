package thito.nodeflow.library.ui;

import javafx.scene.input.*;

public interface WindowHitTest {

    HitTestAction testHit(int screenX, int screenY, MouseButton button);

}
