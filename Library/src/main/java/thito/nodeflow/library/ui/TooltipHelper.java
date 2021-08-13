package thito.nodeflow.library.ui;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.util.*;

public class TooltipHelper {
    public static Tooltip tooltip(I18n text) {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(text);
        return tooltip;
    }
    public static void install(Node node, Tooltip tooltip) {
        uninstall(node);
        TooltipHelper helper = new TooltipHelper(node, tooltip);
        helper.install();
        node.getProperties().put(TooltipHelper.class, helper);
    }

    public static void uninstall(Node node) {
        TooltipHelper helper = (TooltipHelper) node.getProperties().remove(TooltipHelper.class);
        if (helper != null) {
            helper.uninstall();
        }
    }

    private Node node;
    private Tooltip tooltip;

    private EventHandler<MouseEvent> mouseEntered, mouseExited, mousePressed;
    private Timeline showing, idling, hiding;

    private TooltipHelper(Node node, Tooltip tooltip) {
        this.node = node;
        this.tooltip = tooltip;

        tooltip.setOpacity(0);

        showing = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    tooltip.show(node.getScene().getWindow());
                }),
                new KeyFrame(Duration.millis(150), new KeyValue(tooltip.opacityProperty(), 1)));
        showing.setOnFinished(event -> {
            hiding.stop();
            idling.play();
        });

        idling = new Timeline(
                new KeyFrame(Duration.millis(16), event -> {
                    update();
                })
        );

        idling.setCycleCount(Animation.INDEFINITE);

        hiding = new Timeline(
                new KeyFrame(Duration.millis(250), new KeyValue(tooltip.opacityProperty(), 0))
        );

        hiding.setOnFinished(event -> {
            tooltip.hide();
        });

        mouseEntered = event -> {
            event.consume();
            hiding.stop();
            idling.stop();
            showing.play();
        };

        mousePressed = event -> {
            showing.stop();
            idling.stop();
            hiding.play();
        };

        mouseExited = event -> {
            event.consume();
            showing.stop();
            idling.stop();
            hiding.play();
        };
    }

    public void update() {
        Point2D mouse = Toolkit.mouse();
        tooltip.setX(mouse.getX());
        tooltip.setY(mouse.getY());
    }

    public void install() {
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEntered);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExited);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
    }

    public void uninstall() {
        node.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEntered);
        node.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExited);
        node.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
    }
}
