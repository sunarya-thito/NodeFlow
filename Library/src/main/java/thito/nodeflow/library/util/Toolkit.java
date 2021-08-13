package thito.nodeflow.library.util;

import javafx.geometry.*;
import javafx.scene.robot.*;

public class Toolkit {
    private static Robot robot = new Robot();

    public static Point2D mouse() {
        return robot.getMousePosition();
    }
}
