package thito.nodeflow.engine;

import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.engine.handler.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

import java.util.*;

public class Node extends CanvasElement {
    private NodeCanvas canvas;
    private UUID id;

    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();

    // editor
    private BooleanProperty selected = new SimpleBooleanProperty();
    // end editor

    private NodeHandler handler;

    private ObservableList<NodeParameter> parameters = FXCollections.observableArrayList();

    private NodeSkin skin;

    {
        parameters.addListener(new NodeParameterListListener());
        x.addListener((obs, old, val) -> {
            x.set(Math.max(0, val.doubleValue()));
        });
        y.addListener((obs, old, val) -> {
            y.set(Math.max(0, val.doubleValue()));
        });
    }

    public Node() {
    }

    protected void initialize(NodeCanvas canvas) {
        this.canvas = canvas;
        id = UUID.randomUUID();
        handler = canvas.getHandler().createHandler(this, null);
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

    public NodeHandler getHandler() {
        return handler;
    }

    public BooleanProperty selectedProperty() {
        return selected;
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

    public class NodeParameterListListener implements ListChangeListener<NodeParameter> {
        @Override
        public void onChanged(Change<? extends NodeParameter> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (NodeParameter a : c.getAddedSubList()) {
                        a.initialize(Node.this);
                        skin.onParameterAdded(a);
                    }
                }
                if (c.wasRemoved()) {
                    for (NodeParameter r : c.getRemoved()) {
                        skin.onParameterRemoved(r);
                    }
                }
            }
        }
    }
}
