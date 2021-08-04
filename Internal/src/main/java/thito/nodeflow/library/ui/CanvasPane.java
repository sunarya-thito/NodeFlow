package thito.nodeflow.library.ui;

import javafx.scene.canvas.*;
import javafx.scene.layout.*;

public class CanvasPane extends Pane {

    private final Canvas canvas;

    public CanvasPane() {
        canvas = new Canvas();
        getChildren().add(canvas);
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
    }

    public Canvas getCanvas() {
        return canvas;
    }
}