package thito.nodeflow.library.platform.unknown;

import javafx.scene.input.*;
import javafx.stage.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.Window;

public class Unknown_HitTestListener {
    private Window window;

    private boolean press;
    private double offX, offY;
    public Unknown_HitTestListener(Window window) {
        this.window = window;
        window.getStage().getScene().addEventHandler(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                HitTestAction action = window.getWindowHitTest().testHit((int) event.getScreenX(), (int) event.getScreenY(), event.getButton());
                press = false;
                if (action == HitTestAction.CAPTION) {
                    press = true;
                    event.consume();
                    offX = event.getScreenX() - window.getStage().getX();
                    offY = event.getScreenY() - window.getStage().getY();
                }
                return;
            }
            if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                if (press) {
                    event.consume();
                    Stage stage = window.getStage();
                    stage.setX(event.getScreenX() - offX);
                    stage.setY(event.getScreenY() - offY);
                    return;
                }
            }
            HitTestAction action = window.getWindowHitTest().testHit((int) event.getScreenX(), (int) event.getScreenY(), MouseButton.NONE);
            if (action != HitTestAction.CLIENT) {
                event.consume();
            }
        });
    }
}
