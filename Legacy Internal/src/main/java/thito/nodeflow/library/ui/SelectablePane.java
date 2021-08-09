package thito.nodeflow.library.ui;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.util.*;

public class SelectablePane extends StackPane {
    private double x1, y1, x2, y2;
    private boolean multiselect;
    private Pane dragPane = new Pane();
    private Pane dragVisual = new Pane();
    private Parent container;
    public SelectablePane(Parent container) {
        this.container = container;
        getChildren().addAll(container, dragPane);
        dragVisual.setBorder(new Border(new BorderStroke(Color.CORNFLOWERBLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        Color color = Color.CORNFLOWERBLUE;
        dragVisual.setBackground(new Background(new BackgroundFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5f), null, null)));
        dragVisual.setOpacity(0.5);
        dragPane.setMouseTransparent(true);
        dragVisual.setMouseTransparent(true);
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            x1 = x2 = event.getX();
            y1 = y2 = event.getY();
            multiselect = event.isControlDown();
            startDrag();
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            x2 = event.getX();
            y2 = event.getY();
            updateDrag();
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            // using cached value?
            x1 = x2 = y1 = y2 = 0;
            multiselect = false;
            // not sure... is this micro-optimization??
            stopDrag();
        });
    }

    private void startDrag() {
        updateLayout();
        dragPane.getChildren().add(dragVisual);
    }

    private void updateLayout() {
        dragVisual.setLayoutX(Math.min(x1, x2));
        dragVisual.setLayoutY(Math.min(y1, y2));
        dragVisual.setMinWidth(Math.abs(x1 - x2));
        dragVisual.setMinHeight(Math.abs(y1 - y2));
    }

    private void updateDrag() {
        updateLayout();
//        if (Math.abs(x1 - x2) < 2 || Math.abs(y1 - y2) < 2) return;
        Bounds sceneBounds = localToScene(dragVisual.getBoundsInParent());
        List<Node> children = container.getChildrenUnmodifiable();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child instanceof SelectableNode) {
                Bounds childSceneBounds = child.localToScene(child.getBoundsInLocal());
                if (multiselect) {
                    ((SelectableNode) child).setSelected(((SelectableNode) child).isSelected() || childSceneBounds.intersects(sceneBounds));
                } else {
                    ((SelectableNode) child).setSelected(childSceneBounds.intersects(sceneBounds));
                }
            }
        }
    }

    private void stopDrag() {
        dragPane.getChildren().remove(dragVisual);
    }
}
