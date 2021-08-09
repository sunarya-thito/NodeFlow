package thito.nodeflow.library.ui;

import javafx.animation.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;

public class ViewportPane extends Pane {
    private BorderPane current = new BorderPane();
    private BorderPane onGoing = new BorderPane();
    private ViewportTransition transition;
    private Timeline currentTimeline;
    private ObjectProperty<Node> viewport = new SimpleObjectProperty<>();

    public ViewportPane() {
        Toolkit.name(current, "A-Viewport");
        Toolkit.name(onGoing, "B-Viewport");
        current.prefWidthProperty().bind(widthProperty());
        onGoing.prefWidthProperty().bind(widthProperty());

        current.prefHeightProperty().bind(heightProperty());
        onGoing.prefHeightProperty().bind(heightProperty());

        heightProperty().addListener(this::update);
        widthProperty().addListener(this::update);

        getChildren().addAll(current, onGoing);
        viewport.addListener((obs, old, val) -> switchView(val));
    }

    private void update(Observable observable) {
        if (transition != null) {
            transition.updatePosition(this);
        }
    }

    public Pane getCurrentViewportPane() {
        return current;
    }

    public Pane getOnGoingViewportPane() {
        return onGoing;
    }

    public ViewportTransition getTransition() {
        return transition;
    }

    public void setTransition(ViewportTransition transition) {
        this.transition = transition;
        if (transition != null) {
            transition.updatePosition(this);
        }
    }

    public Node getView() {
        return onGoing.getCenter() != null ? onGoing.getCenter() : current.getCenter();
    }

    private void directSwitchView(Node node) {
        if (transition != null) {
            if (current.getCenter() == null) {
                transition.updatePosition(this);
                current.setCenter(node);
                return;
            }
            current.setMouseTransparent(false);
            onGoing.setMouseTransparent(true);
            transition.updatePosition(this);
            onGoing.setCenter(node);
            if (onGoing.getCenter() instanceof ViewportListener) {
                ((ViewportListener) onGoing.getCenter()).onShowing();
            }
            currentTimeline = transition.play(this, () -> {
                synchronized (this) {
                    if (current.getCenter() instanceof ViewportListener) {
                        ((ViewportListener) current.getCenter()).onHiding();
                    }
                    current.setMouseTransparent(true);
                    onGoing.setMouseTransparent(false);
                    BorderPane temp = onGoing;
                    onGoing = current;
                    current = temp;
                    transition.updatePosition(this);
                    currentTimeline = null;
                    onGoing.setCenter(null);
                    if (queued != null) {
                        switchView(queued);
                        queued = null;
                    };
                }
            });
            if (queued != null) {
                currentTimeline.setRate(2);
            }
        } else {
            current.setCenter(node);
            onGoing.setCenter(null);
        }
    }

    private Node queued;

    private void switchView(Node node) {
        synchronized (this) {
            if (currentTimeline != null) {
                currentTimeline.setRate(2); // speed up transition
                queued = node;
            } else {
                directSwitchView(node);
            }
        }
    }

    public Node getViewport() {
        return viewport.get();
    }

    public ObjectProperty<Node> viewportProperty() {
        return viewport;
    }

    public void setViewport(Node viewport) {
        this.viewport.set(viewport);
    }
}
