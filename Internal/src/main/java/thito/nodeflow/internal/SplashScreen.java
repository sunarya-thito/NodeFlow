package thito.nodeflow.internal;

import javafx.animation.*;
import javafx.scene.*;
import javafx.scene.layout.*;

public class SplashScreen {
    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root, -1, -1, false, SceneAntialiasing.BALANCED);

    private final Interpolator spline = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

    public SplashScreen() {
    }

    public Scene getScene() {
        return scene;
    }
}
