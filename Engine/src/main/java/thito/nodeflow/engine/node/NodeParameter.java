package thito.nodeflow.engine.node;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;

import java.util.*;

public class NodeParameter {
    private UUID id;
    private Node node;
    private NodeParameterHandler handler;
    private final ObjectProperty<InsertFunction> nextInsert = new SimpleObjectProperty<>();
    private final ObjectProperty<InsertFunction> previousInsert = new SimpleObjectProperty<>();
    private final BooleanProperty removable = new SimpleBooleanProperty();
    private NodeParameterSkin skin;

    public NodeParameter() {
    }

    public void setHandler(NodeParameterHandler handler) {
        this.handler = handler;
    }

    protected void initialize(Node node) {
        this.node = node;
        id = UUID.randomUUID();
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
