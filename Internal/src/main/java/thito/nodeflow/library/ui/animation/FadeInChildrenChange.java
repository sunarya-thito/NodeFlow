package thito.nodeflow.library.ui.animation;

import javafx.animation.*;
import javafx.scene.*;
import javafx.util.*;

import java.util.*;

public class FadeInChildrenChange implements ChildrenChange {
    @Override
    public void refresh(Parent parent, List<Node> added, List<Node> removed) {
        Timeline timeline = (Timeline) parent.getProperties().computeIfAbsent(FadeInChildrenChange.class, x -> new Timeline());
        timeline.stop();
        timeline.getKeyFrames().clear();
        for (Node child : parent.getChildrenUnmodifiable()) {
            double targetOpacity = child.getOpacity();
            child.setOpacity(0);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(child.opacityProperty(), targetOpacity)));
        }
        timeline.play();
    }
}
