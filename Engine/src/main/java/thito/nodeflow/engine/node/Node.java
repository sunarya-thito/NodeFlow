package thito.nodeflow.engine.node;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.engine.node.handler.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;

import java.util.*;
import java.util.stream.Collectors;

public class Node extends CanvasElement {
    private NodeCanvas canvas;
    private UUID id;

    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    // editor
    private final BooleanProperty selected = new SimpleBooleanProperty();
    // end editor

    protected NodeHandler handler;

    private final ObservableList<NodeParameter> parameters = FXCollections.observableArrayList();

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

    public void setHandler(NodeHandler handler) {
        this.handler = handler;
    }

    protected void initialize(NodeCanvas canvas) {
        if (this.canvas != null) return;
        this.canvas = canvas;
        id = UUID.randomUUID();
        skin = handler.createSkin();
    }

    public Node(NodeCanvas canvas, NodeState state) {
        this.canvas = canvas;
        id = state.id;
        x.set(state.x);
        y.set(state.y);
        handler = createHandler(state.handlerState);
        state.nodeParameterStateList.forEach(parameterState -> {
            parameters.add(new NodeParameter(this, parameterState));
        });
        skin = handler.createSkin();
    }

    protected NodeHandler createHandler(HandlerState state) {
        return canvas.getHandler().createHandler(this, state);
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
        public void onChanged(Change<? extends NodeParameter> change) {
            List<javafx.scene.Node> list = skin.getNodeParameterBox().getChildren();
            while (change.next()) {
                if (change.wasPermutated()) {
                    List<NodeParameterSkin> collect = change.getList().subList(change.getFrom(), change.getTo()).stream().map(x -> {
                        x.initialize(Node.this);
                        return x.getSkin();
                    }).collect(Collectors.toList());
                    Platform.runLater(() -> {
                        list.subList(change.getFrom(), change.getTo()).clear();
                        list.addAll(change.getFrom(), collect);
                    });
                } else {
                    if (change.wasRemoved()) {
                        Platform.runLater(() -> {
                            list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                        });
                    }
                    if (change.wasAdded()) {
                        List<NodeParameterSkin> collect = change.getAddedSubList().stream().map(x -> {
                            x.initialize(Node.this);
                            return x.getSkin();
                        }).collect(Collectors.toList());
                        Platform.runLater(() -> {
                            list.addAll(change.getFrom(), collect);
                        });
                    }
                }
            }
        }
    }
}
