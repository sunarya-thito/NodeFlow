package thito.nodeflow.library.ui;

import com.sun.javafx.stage.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.util.*;

public class LayoutDebugger {

    private Window window;
    private Stage stage;

    private Pane highlightLayer = new Pane();
    private Pane highlight = new Pane();
    private Line vLine1 = new Line();
    private Line vLine2 = new Line();
    private Line hLine1 = new Line();
    private Line hLine2 = new Line();

    private ObjectProperty<Node> selection = new SimpleObjectProperty<>();
    private BooleanProperty lockObject = new SimpleBooleanProperty();

    private ScheduledTask runningTask;

    public LayoutDebugger(Window window) {
        this.window = window;
        initStage();
    }

    protected void initStage() {
        highlightLayer.setMouseTransparent(true);
        highlightLayer.setManaged(false);
        highlightLayer.getChildren().addAll(highlight, vLine1, vLine2, hLine1, hLine2);

        stage = new Stage();

        Stage owner = window.getStage();
        stage.initOwner(owner);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.NONE);

        highlightLayer.visibleProperty().addListener((obs, old, val) -> {
            if (runningTask != null) {
                stage.hide();
                runningTask.cancel();
            }
            if (val) {
                stage.show();
                runningTask = TaskThread.UI().schedule(this::updateSelection, Duration.millis(16), Duration.millis(16));
            }
        });
    }

    public boolean isLockObject() {
        return lockObject.get();
    }

    public BooleanProperty lockObjectProperty() {
        return lockObject;
    }

    public Pane getHighlightLayer() {
        return highlightLayer;
    }

    public BooleanProperty visibleProperty() {
        return highlightLayer.visibleProperty();
    }

    Node findNode(Node parent) {
        Point2D mouse = Toolkit.mouse();
        if (parent instanceof Parent) {
            for (Node child : ((Parent) parent).getChildrenUnmodifiable()) {
                Bounds screenBounds = child.localToScreen(child.getLayoutBounds());
                if (screenBounds.contains(mouse)) {
                    return child;
                }
            }
        }
        Bounds screenBounds = parent.localToScreen(parent.getLayoutBounds());
        if (screenBounds.contains(mouse)) {
            return parent;
        }
        return null;
    }

    public void updateSelection() {
        if (!isLockObject()) {
            selection.set(findNode(window.getStage().getScene().getRoot()));
        }

        Node node = selection.get();

        if (node == null || !highlightLayer.isVisible()) {
            return;
        }

        Bounds local = highlightLayer.sceneToLocal(node.localToScene(node.getLayoutBounds()));

        highlight.setLayoutX(local.getMinX());
        highlight.setLayoutY(local.getMinY());
        highlight.setPrefSize(local.getWidth(), local.getHeight());

        vLine1.setStartX(local.getMinX());
        vLine1.setStartY(0);
        vLine1.setEndX(local.getMinX());
        vLine1.setEndY(highlightLayer.getHeight());

        vLine2.setStartX(local.getMaxX());
        vLine2.setStartY(0);
        vLine2.setEndX(local.getMaxX());
        vLine2.setEndY(highlightLayer.getHeight());

        hLine1.setStartX(0);
        hLine1.setStartY(local.getMinY());
        hLine1.setEndX(highlightLayer.getWidth());
        hLine1.setEndY(local.getMinY());

        hLine2.setStartX(0);
        hLine2.setStartY(local.getMaxY());
        hLine2.setEndX(highlightLayer.getWidth());
        hLine2.setEndY(local.getMaxY());
    }
}
