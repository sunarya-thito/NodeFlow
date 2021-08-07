package thito.nodeflow.engine;

import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.engine.handler.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

import java.util.*;

public class Node {
    private NodeCanvas canvas;
    private UUID id;

    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();

    private NodeHandler handler;

    private ObservableList<NodeParameter> parameters = FXCollections.observableArrayList();

    private NodeSkin skin;

    public Node() {
    }

    protected void initialize(NodeCanvas canvas) {
        this.canvas = canvas;
        id = UUID.randomUUID();
        skin = handler.createSkin();
    }

    public Node(NodeCanvas canvas, NodeState state) {
        this.canvas = canvas;
        id = state.id;
        x.set(state.x);
        y.set(state.y);
        handler = canvas.getHandler().createHandler(this, state.handlerState);
        state.nodeParameterStateList.forEach(parameterState -> {
            parameters.add(new NodeParameter(this, parameterState));
        });
        skin = handler.createSkin();
    }

    public NodeSkin getSkin() {
        return skin;
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public UUID getId() {
        return id;
    }

    public ObservableList<NodeParameter> getParameters() {
        return parameters;
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    public NodeState saveState() {
        NodeState state = new NodeState();
        state.id = id;
        state.handlerState = handler.saveState();
        state.x = x.get();
        state.y = y.get();
        parameters.stream().map(NodeParameter::saveState).forEach(state.nodeParameterStateList::add);
        return state;
    }
}
