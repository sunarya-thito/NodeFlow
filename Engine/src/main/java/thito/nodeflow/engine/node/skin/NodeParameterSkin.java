package thito.nodeflow.engine.node.skin;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.engine.node.*;

public class NodeParameterSkin extends Skin {
    private final NodeParameter parameter;
    protected final BorderPane editorPane = new BorderPane();
    private final Label title = new Label();
    private final HBox box = new HBox();
    private Node inputNode, outputNode;

    public NodeParameterSkin(NodeParameter parameter) {
        this.parameter = parameter;
        skin(title, "NodeParameterTitle");
        skin(box, "NodeParameterContainer");
        box.getChildren().addAll(title, editorPane);
        title.textProperty().bind(parameter.getHandler().displayNameProperty());
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            for (CanvasElement e : parameter.getNode().getCanvas().getSelectedElements()) {
                if (e instanceof thito.nodeflow.engine.node.Node && e != parameter.getNode()) {
                    ((NodeSkin) e.getSkin()).markParameterInactive();
                    ((NodeSkin) e.getSkin()).getPopup().hide();
                }
            }
            parameter.getNode().getSkin().openPopUp();
            parameter.getNode().getSkin().setPopUpContent(parameter);
        });
        Pane portPane = new Pane();
        portPane.setManaged(false);
        NodePort inputPort = parameter.getHandler().getInputPort();
        NodePort outputPort = parameter.getHandler().getOutputPort();
        if (inputPort != null && outputPort != null) {
            box.setAlignment(Pos.CENTER);
        } else if (inputPort != null) {
            box.setAlignment(Pos.CENTER_LEFT);
        } else {
            box.setAlignment(Pos.CENTER_RIGHT);
        }
        if (inputPort != null) {
            PortShape.Handler handler = inputPort.getShape().createHandler();
            handler.colorProperty().bind(inputPort.colorProperty());
            inputNode = handler.impl_getPeer();
            inputNode.setPickOnBounds(false);
            inputNode.translateXProperty().bind(((Region) inputNode).widthProperty().divide(2).negate());
            inputNode.translateYProperty().bind(((Region) inputNode).heightProperty().divide(2).negate());
            inputNode.layoutYProperty().bind(heightProperty().subtract(heightProperty().divide(2)));
            inputNode.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                NodeCanvasSkin skin = getParameter().getNode().getCanvas().getSkin();
                skin.beginLinkingDrag(event.getSceneX(), event.getSceneY(), null, getParameter(), event.isShiftDown());
            });
            inputNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                NodeCanvasSkin skin = getParameter().getNode().getCanvas().getSkin();
                skin.dragLinking(event.getSceneX(), event.getSceneY());
            });
            inputNode.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                NodeCanvasSkin skin = getParameter().getNode().getCanvas().getSkin();
                skin.stopLinkingDrag(event.getSceneX(), event.getSceneY(), true);
            });
            inputNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                for (NodeLinked linked : parameter.getNode().getCanvas().getNodeLinkedList()) {
                    if (linked.getTarget() == parameter) {
                        linked.styleHandlerProperty().get().requestHighlight().add(inputNode);
                    }
                }
            });
            inputNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                for (NodeLinked linked : parameter.getNode().getCanvas().getNodeLinkedList()) {
                    if (linked.getTarget() == parameter) {
                        linked.styleHandlerProperty().get().requestHighlight().remove(inputNode);
                    }
                }
            });
            inputNode.addEventHandler(MouseEvent.ANY, Event::consume);
            skin(inputNode, "InputPort");
            portPane.getChildren().add(inputNode);
        }
        if (outputPort != null) {
            PortShape.Handler handler = outputPort.getShape().createHandler();
            handler.colorProperty().bind(outputPort.colorProperty());
            outputNode = handler.impl_getPeer();
            outputNode.setPickOnBounds(false);
            outputNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                for (NodeLinked linked : parameter.getNode().getCanvas().getNodeLinkedList()) {
                    if (linked.getSource() == parameter) {
                        linked.styleHandlerProperty().get().requestHighlight().add(outputNode);
                    }
                }
            });
            outputNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                for (NodeLinked linked : parameter.getNode().getCanvas().getNodeLinkedList()) {
                    if (linked.getSource() == parameter) {
                        linked.styleHandlerProperty().get().requestHighlight().remove(outputNode);
                    }
                }
            });
            skin(outputNode, "OutputPort");
            outputNode.translateXProperty().bind(((Region) outputNode).widthProperty().divide(2).negate());
            outputNode.translateYProperty().bind(((Region) outputNode).heightProperty().divide(2).negate());
            outputNode.layoutXProperty().bind(widthProperty());
            outputNode.layoutYProperty().bind(heightProperty().subtract(heightProperty().divide(2)));
            outputNode.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                NodeCanvasSkin skin = getParameter().getNode().getCanvas().getSkin();
                skin.beginLinkingDrag(event.getSceneX(), event.getSceneY(), getParameter(), null, event.isShiftDown());
            });
            outputNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                NodeCanvasSkin skin = getParameter().getNode().getCanvas().getSkin();
                skin.dragLinking(event.getSceneX(), event.getSceneY());
            });
            outputNode.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                NodeCanvasSkin skin = getParameter().getNode().getCanvas().getSkin();
                skin.stopLinkingDrag(event.getSceneX(), event.getSceneY(), true);
            });
            portPane.getChildren().add(outputNode);
        }
        getChildren().addAll(box, portPane);
    }

    public Node getInputNode() {
        return inputNode;
    }

    public Node getOutputNode() {
        return outputNode;
    }

    public NodeParameter getParameter() {
        return parameter;
    }
}
