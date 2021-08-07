package thito.nodeflow.engine;

import javafx.beans.property.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

import java.util.*;

public class NodeGroup {
    private NodeCanvas canvas;
    private UUID id;
    private StringProperty name = new SimpleStringProperty("");
    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();
    private DoubleProperty width = new SimpleDoubleProperty();
    private DoubleProperty height = new SimpleDoubleProperty();
    private NodeGroupSkin skin;

    public NodeGroup() {
    }

    protected void initialize(NodeCanvas canvas) {
        this.canvas = canvas;
        this.id = id;
        this.skin = canvas.getHandler().createGroupSkin();
    }

    public NodeGroup(NodeCanvas canvas, NodeGroupState state) {
        this.canvas = canvas;
        id = state.id;
        name.set(state.name);
        x.set(state.x);
        y.set(state.y);
        width.set(state.width);
        height.set(state.height);
        skin = canvas.getHandler().createGroupSkin();
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
