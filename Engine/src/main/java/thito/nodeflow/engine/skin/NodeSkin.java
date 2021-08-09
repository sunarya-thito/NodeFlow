package thito.nodeflow.engine.skin;

import javafx.application.*;
import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.value.*;
import javafx.css.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import thito.nodeflow.engine.*;
import thito.nodeflow.engine.util.*;

public class NodeSkin extends Skin {
    public static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private Node node;
    private VBox nodeParameterBox = new VBox();
    private CalloutsPopup popup = new CalloutsPopup();
    private Label popupTitle;
    private NodeParameter parameterPopUp;

    public NodeSkin(Node node) {
        setPickOnBounds(false);
        this.node = node;
        layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            if (node.getCanvas().snapToGridProperty().get()) {
                return NodeHelper.floor(node.xProperty().get(), 20);
            } else {
                return node.xProperty().get();
            }
        }, node.xProperty(), node.getCanvas().snapToGridProperty()));
        layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            if (node.getCanvas().snapToGridProperty().get()) {
                return NodeHelper.floor(node.yProperty().get(), 20);
            } else {
                return node.yProperty().get();
            }
        }, node.yProperty(), node.getCanvas().snapToGridProperty()));
        node.selectedProperty().addListener((obs, old, val) -> {
            pseudoClassStateChanged(SELECTED, val);
            if (!val) {
                markParameterInactive();
                popup.hide();
            }
        });
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            setPopUpContent(null);
            for (CanvasElement e : getNode().getCanvas().getSelectedElements()) {
                if (e instanceof Node && e != node) {
                    ((NodeSkin) e.getSkin()).markParameterInactive();
                    ((NodeSkin) e.getSkin()).popup.hide();
                }
            }
            openPopUp();
        });
        skin(nodeParameterBox, "NodeParameterBox");
        initializeSkin();
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            event.consume();
            if (event.getButton() == MouseButton.PRIMARY) {
                NodeSkin.this.toFront();
                if (!node.selectedProperty().get()) {
                    if (!event.isShiftDown()) {
                        node.getCanvas().getSelectedElements().stream().filter(x -> x != node).forEach(e -> e.selectedProperty().set(false));
                    }
                    node.selectedProperty().set(true);
                }
            }
        });
        addEventHandler(MouseEvent.ANY, Event::consume);
        layoutXProperty().addListener(o -> updatePopUpPositionLater());
        layoutYProperty().addListener(o -> updatePopUpPositionLater());
        heightProperty().addListener(o -> updatePopUpPositionLater());
        ChangeListener<Boolean> focusListener = (obs, old, val) -> {
            if (!val) {
                popup.hide();
                markParameterInactive();
            }
        };
        InvalidationListener positionListener = obs -> updatePopUpPosition();
        ChangeListener<Window> windowChangeListener = (obs, old, val) -> {
            if (old != null) {
                popup.hide();
                markParameterInactive();
                old.focusedProperty().removeListener(focusListener);
                old.xProperty().removeListener(positionListener);
                old.yProperty().removeListener(positionListener);
            }
            if (val != null) {
                val.focusedProperty().addListener(focusListener);
                val.xProperty().addListener(positionListener);
                val.yProperty().addListener(positionListener);
            }
        };
        sceneProperty().addListener((obs, old, val) -> {
            if (old != null) {
                popup.hide();
                markParameterInactive();
                old.windowProperty().removeListener(windowChangeListener);
            }
            if (val != null) {
                val.windowProperty().addListener(windowChangeListener);
            }
        });
    }

    private void updatePopUpPositionLater() {
        Platform.runLater(() -> {
            Point2D location = localToScreen(0, getHeight());
            popup.setX(location.getX() - 20);
            popup.setY(location.getY() - 15);
        });
    }

    private void updatePopUpPosition() {
        Point2D location = localToScreen(0, getHeight());
        popup.setX(location.getX() - 20);
        popup.setY(location.getY() - 15);
    }


    public void openPopUp() {
        if (popup.isShowing()) return;
        updatePopUpPosition();
        popup.setAutoFix(false);
        popup.show(getScene().getWindow());
    }

    public CalloutsPopup getPopup() {
        return popup;
    }

    public void markParameterInactive() {
        if (parameterPopUp != null) {
            parameterPopUp.getSkin().pseudoClassStateChanged(SELECTED, false);
        }
    }
    public void setPopUpContent(NodeParameter parameter) {
        markParameterInactive();
        parameterPopUp = parameter;
        if (parameter == null) {
            popupTitle.textProperty().bind(node.getHandler().displayNameProperty());
        } else {
            parameter.getSkin().pseudoClassStateChanged(SELECTED, true);
            popupTitle.textProperty().bind(node.getHandler().displayNameProperty().concat(" - ").concat(parameter.getHandler().displayNameProperty()));
        }
    }

    protected void initializeSkin() {
        VBox box = new VBox();
        box.setPickOnBounds(false);
        box.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            for (NodeLinked linked : node.getCanvas().getNodeLinkedList()) {
                if (linked.getSource().getNode() == node || linked.getTarget().getNode() == node) {
                    linked.styleHandlerProperty().get().requestHighlight().add(node);
                }
            }
        });
        box.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            for (NodeLinked linked : node.getCanvas().getNodeLinkedList()) {
                if (linked.getSource().getNode() == node || linked.getTarget().getNode() == node) {
                    linked.styleHandlerProperty().get().requestHighlight().remove(node);
                }
            }
        });
        box.getChildren().addAll(new NodeTitlePane(), nodeParameterBox);
        getChildren().addAll(box);

        BorderPane popupContent = popup.getSkin().getContent();
        popupTitle = new Label();
        skin(popupTitle, "NodePopUpTitle");
        popupContent.setTop(popupTitle);
    }

    public void onParameterAdded(NodeParameter p) {
        nodeParameterBox.getChildren().add(p.getSkin());
    }

    public void onParameterRemoved(NodeParameter p) {
        nodeParameterBox.getChildren().remove(p.getSkin());
    }

    public Node getNode() {
        return node;
    }

    public class NodeTitlePane extends HBox {
        private double dragX, dragY;
        private boolean dragging;
        public NodeTitlePane() {
            skin(this, "NodeTitlePane");
            Label title = skin(new Label(), "NodeTitleLabel");
            title.textProperty().bind(node.getHandler().displayNameProperty());
            ImageView icon = skin(new ImageView(), "NodeTitleIcon");
            icon.imageProperty().bind(node.getHandler().iconProperty());
            getChildren().addAll(title);

            addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                event.consume();
                if (event.getButton() == MouseButton.PRIMARY) {
                    NodeSkin.this.toFront();
                    if (!node.selectedProperty().get()) {
                        if (!event.isShiftDown()) {
                            node.getCanvas().getSelectedElements().stream().filter(x -> x != node).forEach(e -> e.selectedProperty().set(false));
                        }
                        node.selectedProperty().set(true);
                    }
                    dragX = event.getSceneX();
                    dragY = event.getSceneY();
                    for (CanvasElement e : node.getCanvas().getSelectedElements()) {
                        CanvasElement.DragInfo dragInfo = e.getDragInfo();
                        dragInfo.setX(e.xProperty().get());
                        dragInfo.setY(e.yProperty().get());
                    }
                    dragging = true;
                }
            });

            addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                event.consume();
                if (dragging) {
                    for (CanvasElement e : node.getCanvas().getSelectedElements()) {
                        if (e instanceof thito.nodeflow.engine.Node) {
                            ((NodeSkin) e.getSkin()).markParameterInactive();
                            ((NodeSkin) e.getSkin()).getPopup().hide();
                        }
                        CanvasElement.DragInfo dragInfo = e.getDragInfo();
                        double targetX = event.getSceneX() - dragX + dragInfo.getX();
                        double targetY = event.getSceneY() - dragY + dragInfo.getY();
                        e.xProperty().set(targetX);
                        e.yProperty().set(targetY);
                    }
                }
            });

            addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                event.consume();
                dragging = false;
            });
        }
    }
}
