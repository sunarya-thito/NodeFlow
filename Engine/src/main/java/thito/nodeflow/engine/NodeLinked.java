package thito.nodeflow.engine;

import javafx.beans.property.*;
import thito.nodeflow.engine.state.*;

import java.util.*;

public class NodeLinked implements NodeLink {
    private NodeCanvas canvas;
    private NodeParameter source;
    private NodeParameter target;
    private ObjectProperty<NodeLinkStyle.Handler> styleHandler = new SimpleObjectProperty<>();

    public NodeLinked(NodeCanvas canvas, NodeParameter source, NodeParameter target) {
        this.canvas = canvas;
        this.source = source;
        this.target = target;
    }

    public NodeLinked(NodeCanvas canvas, NodeLinkedState state) {
        this.canvas = canvas;
        source = canvas.findParameter(state.sourceId);
        target = canvas.findParameter(state.targetId);
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
    public ObjectProperty<NodeLinkStyle.Handler> styleHandlerProperty() {
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
