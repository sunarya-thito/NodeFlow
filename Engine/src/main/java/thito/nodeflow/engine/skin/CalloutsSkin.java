package thito.nodeflow.engine.skin;

import javafx.scene.layout.*;
import javafx.scene.shape.*;

public class CalloutsSkin extends Skin {
    static double height(double width) {
        return Math.sqrt(Math.pow(width, 2) - Math.pow(width / 2, 2));
    }
    private Polygon pointer = new Polygon(10, 0, 0, height(20), 20, height(20));
    private BorderPane content = new BorderPane();
    public CalloutsSkin() {
        getChildren().addAll(pointer, content);
        pointer.setLayoutX(10);
        content.setLayoutY(height(19));
        skin(pointer, "CalloutsPointer");
        skin(content, "CalloutsContent");
    }

    public BorderPane getContent() {
        return content;
    }

    public Polygon getPointer() {
        return pointer;
    }
}
