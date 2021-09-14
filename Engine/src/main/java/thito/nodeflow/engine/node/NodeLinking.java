package thito.nodeflow.engine.node;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import thito.nodeflow.engine.node.state.*;

import java.util.*;

public class NodeLinking implements NodeLink {
    private final NodeCanvas canvas;
    private final NodeParameter source;
    private final NodeParameter target;
    private final ObjectProperty<LinkStyle.Handler> styleHandler = new SimpleObjectProperty<>();
    private PortShape.Handler shape;
    private final DoubleProperty tailX = new SimpleDoubleProperty();
    private final DoubleProperty tailY = new SimpleDoubleProperty();

    public NodeLinking(NodeCanvas canvas, NodeParameter source, NodeParameter target) {
        this.canvas = canvas;
        this.source = source;
        this.target = target;
        this.styleHandler.set(canvas.linkStyleProperty().get().createHandler(this));

        if (source != null) {
            source.getSkin().layoutXProperty().addListener(o -> update());
            source.getSkin().layoutYProperty().addListener(o -> update());
            source.getSkin().widthProperty().addListener(o -> update());
            source.getSkin().heightProperty().addListener(o -> update());
            shape = source.getHandler().getOutputPort().getShape().createHandler();
            shape.colorProperty().bind(source.getHandler().getOutputPort().colorProperty());
            styleHandler.get().fillProperty().bind(source.getHandler().getOutputPort().colorProperty());
        } else if (target != null) {
            target.getSkin().layoutXProperty().addListener(o -> update());
            target.getSkin().layoutYProperty().addListener(o -> update());
            target.getSkin().widthProperty().addListener(o -> update());
            target.getSkin().heightProperty().addListener(o -> update());
            shape = target.getHandler().getInputPort().getShape().createHandler();
            shape.colorProperty().bind(target.getHandler().getInputPort().colorProperty());
            styleHandler.get().fillProperty().bind(target.getHandler().getInputPort().colorProperty());
        }

        if (styleHandler.get() != null) {
            styleHandler.get().impl_getPeer().parentProperty().addListener((obs, old, val) -> {
                canvas.getSkin().getLinkingLayer().getChildren().remove(shape.impl_getPeer());
                if (val != null) {
                    canvas.getSkin().getLinkingLayer().getChildren().add(shape.impl_getPeer());
                }
            });
        }

        canvas.getSkin().layoutXProperty().addListener(o -> update());
        canvas.getSkin().layoutYProperty().addListener(o -> update());
        canvas.getSkin().widthProperty().addListener(o -> update());
        canvas.getSkin().heightProperty().addListener(o -> update());

        tailX.addListener(o -> update());
        tailY.addListener(o -> update());

        styleHandler.get().impl_getPeer().parentProperty().addListener(o -> update());
    }

    public DoubleProperty tailXProperty() {
        return tailX;
    }

    public DoubleProperty tailYProperty() {
        return tailY;
    }

    private void update() {
        LinkStyle.Handler handler = styleHandler.get();
        if (handler != null) {
            if (source != null) {
                Point2D point = source.getSkin().localToScene(source.getSkin().getWidth(), source.getSkin().getHeight() / 2d);
                handler.sourceXProperty().set(point.getX());
                handler.sourceYProperty().set(point.getY());
                handler.targetXProperty().set(tailX.get());
                handler.targetYProperty().set(tailY.get());
            } else if (target != null) {
                Point2D point = target.getSkin().localToScene(0, target.getSkin().getHeight() / 2d);
                handler.targetXProperty().set(point.getX());
                handler.targetYProperty().set(point.getY());
                handler.sourceXProperty().set(tailX.get());
                handler.sourceYProperty().set(tailY.get());
            }
        }
        Point2D tailLocal = canvas.getSkin().getLinkingLayer().sceneToLocal(tailX.get(), tailY.get());
        shape.impl_getPeer().setLayoutX(tailLocal.getX() - ((Region) shape.impl_getPeer()).getWidth() / 2d);
        shape.impl_getPeer().setLayoutY(tailLocal.getY() - ((Region) shape.impl_getPeer()).getHeight() / 2d);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeLinking)) return false;
        NodeLinking that = (NodeLinking) o;
        return Objects.equals(canvas, that.canvas) && Objects.equals(source, that.source) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(canvas, source, target);
    }

    @Override
    public ObjectProperty<LinkStyle.Handler> styleHandlerProperty() {
        return styleHandler;
    }

    @Override
    public NodeCanvas getCanvas() {
        return canvas;
    }

    @Override
    public NodeParameter getSource() {
        return source;
    }

    @Override
    public NodeParameter getTarget() {
        return target;
    }

    public NodeLinkedState saveState() {
        NodeLinkedState state = new NodeLinkedState();
        state.sourceId = source.getId();
        state.targetId = target.getId();
        return state;
    }
}
