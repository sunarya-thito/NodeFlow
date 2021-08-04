package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.node.search.*;

public class NodeContextMenu extends SimpleContextMenu {

    private NodeContextMenuPeer peer;
    private ObjectProperty<Node> component = new SimpleObjectProperty<>();
    private ExpectingType type;
    private boolean input;
    private double x, y;
    private NodeParameter parameter;
    private boolean implementation;

    public NodeContextMenu(WindowImpl owner, NodeModule module, double x, double y, ExpectingType type, boolean input, NodeParameter parameter, boolean implementation) {
        super(owner);
        this.parameter = parameter;
        this.type = type;
        this.input = input;
        this.x = x;
        this.y = y;
        this.implementation = implementation;
        peer = new NodeContextMenuPeer(this, module);
        setViewport(peer);
    }

    public boolean isImplementation() {
        return implementation;
    }

    public NodeParameter getParameter() {
        return parameter;
    }

    public ExpectingType getType() {
        return type;
    }

    public boolean isInput() {
        return input;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    protected void attemptUnfocusClose() {
        Task.runOnForeground("check-focus", () -> {
            if (getStage().isFocused()) return;
            super.attemptUnfocusClose();
        });
    }

    public ObjectProperty<Node> componentProperty() {
        return component;
    }

}
