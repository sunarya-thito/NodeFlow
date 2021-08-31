package thito.nodeflow.engine.node;

import javafx.beans.property.*;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.*;

import java.util.*;

public class NodeGroup extends CanvasElement {
    private NodeCanvas canvas;
    private UUID id;
    private final StringProperty name = new SimpleStringProperty("");
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private NodeGroupSkin skin;

    private final BooleanProperty selected = new SimpleBooleanProperty();

    public NodeGroup() {
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    protected void initialize(NodeCanvas canvas) {
        if (this.canvas != null) return;
        this.canvas = canvas;
        this.id = UUID.randomUUID();
        this.skin = canvas.getHandler().createGroupSkin(this);
    }

    public NodeGroup(NodeCanvas canvas, NodeGroupState state) {
        this.canvas = canvas;
        id = state.id;
        name.set(state.name);
        x.set(state.x);
        y.set(state.y);
        width.set(state.width);
        height.set(state.height);
        skin = canvas.getHandler().createGroupSkin(this);
    }

    public NodeGroupSkin getSkin() {
        return skin;
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    public UUID getId() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public NodeGroupState saveState() {
        NodeGroupState state = new NodeGroupState();
        state.id = id;
        state.name = name.get();
        state.x = x.get();
        state.y = y.get();
        state.width = width.get();
        state.height = height.get();
        return state;
    }
}
