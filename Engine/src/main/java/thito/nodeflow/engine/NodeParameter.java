package thito.nodeflow.engine;

import javafx.beans.property.*;
import thito.nodeflow.engine.handler.*;
import thito.nodeflow.engine.skin.*;
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
    private NodeParameterSkin skin;

    public NodeParameter() {
    }

    protected void initialize(Node node) {
        this.node = node;
        id = UUID.randomUUID();
        handler = node.getHandler().createParameterHandler(this, null);
        skin = handler.createSkin();
    }

    public NodeParameter(Node node, NodeParameterState state) {
        this.node = node;
        id = state.id;
        handler = node.getHandler().createParameterHandler(this, state.handlerState);
        skin = handler.createSkin();
    }

    public Collection<? extends NodeParameter> getPairs(boolean asInput) {
        return getNode().getCanvas().getPairings(this, asInput);
    }

    public NodeParameterSkin getSkin() {
        return skin;
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
