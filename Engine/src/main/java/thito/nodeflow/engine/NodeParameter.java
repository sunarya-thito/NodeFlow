package thito.nodeflow.engine;

import javafx.beans.property.*;
import thito.nodeflow.engine.handler.*;
import thito.nodeflow.engine.state.*;

import java.util.*;
import java.util.function.*;

public class NodeParameter {
    private UUID id;
    private Node node;
    private NodeParameterHandler handler;
    private ObjectProperty<InsertFunction> nextInsert = new SimpleObjectProperty<>();
    private ObjectProperty<InsertFunction> previousInsert = new SimpleObjectProperty<>();
    private BooleanProperty removable = new SimpleBooleanProperty();

    public NodeParameter(Node node, NodeParameterState state) {
        this.node = node;
        id = state.id;
        handler = node.getCanvas().getHandler().createParameterHandler(this, state.handlerState);
    }

    public BooleanProperty removableProperty() {
        return removable;
    }

    public ObjectProperty<InsertFunction> nextInsertProperty() {
        return nextInsert;
    }

    public ObjectProperty<InsertFunction> previousInsertProperty() {
        return previousInsert;
    }

    public Node getNode() {
        return node;
    }

    public NodeParameterHandler getHandler() {
        return handler;
    }

    public UUID getId() {
        return id;
    }

    public NodeParameterState saveState() {
        NodeParameterState state = new NodeParameterState();
        state.id = id;
        state.handlerState = handler.saveState();
        return state;
    }
}
