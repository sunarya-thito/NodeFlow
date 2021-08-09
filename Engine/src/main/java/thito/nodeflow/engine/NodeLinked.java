package thito.nodeflow.engine;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.engine.state.*;

import java.util.*;

public class NodeLinked implements NodeLink {
    private NodeCanvas canvas;
    private NodeParameter source;
    private NodeParameter target;
    private ObjectProperty<LinkStyle.Handler> styleHandler = new SimpleObjectProperty<>();

    public NodeLinked(NodeCanvas canvas, NodeParameter source, NodeParameter target) {
        this.canvas = canvas;
        this.source = source;
        this.target = target;
        this.styleHandler.set(canvas.linkStyleProperty().get().createHandler(this));
        for (Region affecting : new Region[] {source.getSkin(), target.getSkin(), source.getNode().getSkin(), target.getNode().getSkin()}) {
            affecting.layoutXProperty().addListener(obs -> update());
            affecting.layoutYProperty().addListener(obs -> update());
            affecting.widthProperty().addListener(obs -> update());
            affecting.heightProperty().addListener(obs -> update());
        }
        update();
    }

    public NodeLinked(NodeCanvas canvas, NodeLinkedState state) {
        this.canvas = canvas;
        source = canvas.findParameter(state.sourceId);
        target = canvas.findParameter(state.targetId);
    }

    private void update() {
        LinkStyle.Handler handler = styleHandler.get();
        if (handler != null) {
            Point2D point = source.getSkin().localToScene(source.getSkin().getWidth(), source.getSkin().getHeight() / 2d);
            handler.sourceXProperty().set(point.getX());
            handler.sourceYProperty().set(point.getY());
            point = target.getSkin().localToScene(0, target.getSkin().getHeight() / 2d);
            handler.targetXProperty().set(point.getX());
            handler.targetYProperty().set(point.getY());
//            double distanceY = Math.abs(handler.targetYProperty().get() - handler.sourceYProperty().get());
            double distanceX = handler.targetXProperty().get() - handler.sourceXProperty().get();
            handler.fillProperty().set(new LinearGradient(0, 0, 1, 0, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(distanceX > 0 ? 0 : 1, source.getHandler().getOutputPort().getColor()),
                    new Stop(distanceX > 0 ? 1 : 0, target.getHandler().getInputPort().getColor())));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeLinked)) return false;
        NodeLinked that = (NodeLinked) o;
        return canvas.equals(that.canvas) && (source.equals(that.source) || source.equals(that.target)) && (target.equals(that.target) || target.equals(that.source));
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
