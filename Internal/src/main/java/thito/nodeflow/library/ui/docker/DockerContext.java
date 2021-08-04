package thito.nodeflow.library.ui.docker;

import javafx.beans.*;
import javafx.scene.input.*;

public interface DockerContext {
//    Paint DOCKER_FOCUS = Color.rgb(52, 140, 235);
//    Paint DOCKER_LOST_FOCUS = Color.rgb(148, 148, 148);
//    Paint HOVERED_DOCKER = Color.color(0, 0, 0, 0.35);
//    Paint SELECTED_DOCKER = Color.color(1, 1, 1, 0.15);
//    Paint HOVERED_SELECTED_DOCKER = Color.color(0, 0, 0, 0.25);
    DataFormat DOCKER_FORMAT = new DataFormat("thito.ui.docker");
    double TOP_PADDING = 3, SIDE_PADDING = 12;
    double CONTROL_LENGTH = 5;
    static InvalidationListener debug() {
        return observable -> System.out.println(observable);
    }
}
