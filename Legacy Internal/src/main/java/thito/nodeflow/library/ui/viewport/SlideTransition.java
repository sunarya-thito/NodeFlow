package thito.nodeflow.library.ui.viewport;

import javafx.animation.*;
import javafx.scene.layout.*;
import javafx.util.*;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class SlideTransition implements ViewportTransition {
    private SlideDirection direction;
    private double gap;
    private Duration duration;
    private Interpolator interpolator;
    private boolean fade;
    private boolean scale;

    public SlideTransition(SlideDirection direction, double gap, Duration duration, Interpolator interpolator, boolean fade, boolean scale) {
        this.direction = direction;
        this.gap = gap;
        this.duration = duration;
        this.interpolator = interpolator;
        this.fade = fade;
        this.scale = scale;
    }

    public void setDirection(SlideDirection direction) {
        this.direction = direction;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setFade(boolean fade) {
        this.fade = fade;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public void updatePosition(ViewportPane viewportPane) {
        Pane current = viewportPane.getCurrentViewportPane();
        Pane onGoing = viewportPane.getOnGoingViewportPane();
        if (fade) {
            onGoing.setOpacity(0);
        }
        if (scale) {
            onGoing.setScaleY(0.5);
            onGoing.setScaleX(0.5);
        }
        switch (direction) {
            case TOP:
                current.layoutXProperty().set(0);
                current.layoutYProperty().set(0);
                onGoing.layoutXProperty().set(0);
                onGoing.layoutYProperty().set(-onGoing.getHeight() - gap);
                break;
            case BOTTOM:
                current.layoutXProperty().set(0);
                current.layoutYProperty().set(0);
                onGoing.layoutXProperty().set(0);
                onGoing.layoutYProperty().set(current.getHeight() + gap);
                break;
            case RIGHT:
                current.layoutXProperty().set(0);
                current.layoutYProperty().set(0);
                onGoing.layoutXProperty().set(current.getWidth() + gap);
                onGoing.layoutYProperty().set(0);
                break;
            case LEFT:
                current.layoutXProperty().set(0);
                current.layoutYProperty().set(0);
                onGoing.layoutXProperty().set(-onGoing.getWidth() - gap);
                onGoing.layoutYProperty().set(0);
                break;
        }
    }

    @Override
    public Timeline play(ViewportPane viewportPane, Runnable onDone) {
        Timeline timeline = new Timeline();
        Pane current = viewportPane.getCurrentViewportPane();
        Pane onGoing = viewportPane.getOnGoingViewportPane();
        List<KeyValue> values = new ArrayList<>();
        if (direction == SlideDirection.TOP) {
            values.add(new KeyValue(current.layoutYProperty(), onGoing.getHeight() + gap, interpolator));
            values.add(new KeyValue(onGoing.layoutYProperty(), 0, interpolator));
        } else if (direction == SlideDirection.BOTTOM) {
            values.add(new KeyValue(current.layoutYProperty(), -current.getHeight() - gap, interpolator));
            values.add(new KeyValue(onGoing.layoutYProperty(), 0, interpolator));
        } else if (direction == SlideDirection.LEFT) {
            values.add(new KeyValue(current.layoutXProperty(), onGoing.getWidth() + gap, interpolator));
            values.add(new KeyValue(onGoing.layoutXProperty(), 0, interpolator));
        } else {
            values.add(new KeyValue(current.layoutXProperty(), -current.getWidth() - gap, interpolator));
            values.add(new KeyValue(onGoing.layoutXProperty(), 0, interpolator));
        }
        if (fade) {
            values.add(new KeyValue(onGoing.opacityProperty(), 1));
            values.add(new KeyValue(current.opacityProperty(), 0));
        }
        if (scale) {
            values.add(new KeyValue(onGoing.scaleXProperty(), 1));
            values.add(new KeyValue(onGoing.scaleYProperty(), 1));
            values.add(new KeyValue(current.scaleXProperty(), 0.5));
            values.add(new KeyValue(current.scaleYProperty(), 0.5));
        }
        timeline.getKeyFrames().add(new KeyFrame(duration, values.toArray(new KeyValue[0])));
        timeline.setOnFinished(event -> onDone.run());
        timeline.play();
        return timeline;
    }

    public enum SlideDirection {
        TOP, BOTTOM, RIGHT, LEFT;
    }
}
