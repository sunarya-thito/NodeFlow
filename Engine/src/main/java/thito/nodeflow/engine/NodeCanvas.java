package thito.nodeflow.engine;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import thito.nodeflow.engine.handler.*;
import thito.nodeflow.engine.skin.*;
import thito.nodeflow.engine.state.*;

import java.util.*;

public class NodeCanvas {
    private DoubleProperty offsetX = new SimpleDoubleProperty();
    private DoubleProperty offsetY = new SimpleDoubleProperty();
    private DoubleProperty scale = new SimpleDoubleProperty();

    private ObservableList<Node> nodeList = FXCollections.observableArrayList();
    private ObservableList<NodeLinked> nodeLinkedList = FXCollections.observableArrayList();
    private ObservableList<NodeGroup> groupList = FXCollections.observableArrayList();

    private NodeCanvasHandler handler;
    private NodeCanvasSkin skin;

    private ObjectProperty<NodeLinkStyle> linkStyle = new SimpleObjectProperty<>();

    public NodeCanvas(NodeCanvasHandler handler) {
        this.handler = handler;
        skin = handler.createCanvasSkin();

        nodeList.addListener(new NodeCanvasNodeListListener());
        nodeLinkedList.addListener(new NodeCanvasNodeLinkedListListener());
        groupList.addListener(new NodeCanvasNodeGroupListListener());
    }

    public boolean disconnect(NodeParameter a, NodeParameter b) {
        for (int i = nodeLinkedList.size() - 1; i >= 0; i--) {
            NodeLink link = nodeLinkedList.get(i);
            if ((link.getSource().equals(a) || link.getSource().equals(a))
                    && (link.getSource().equals(b) || link.getTarget().equals(b)) && !a.equals(b)) {
                nodeLinkedList.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean connect(NodeParameter source, NodeParameter target, boolean force) {
        NodeLinked linked = new NodeLinked(this, source, target);
        if (nodeLinkedList.contains(linked)) return false;
        if (!force) {
            if (!target.getHandler().acceptPairing(source, true)) return false;
            if (!source.getHandler().acceptPairing(target, false)) return false;
        }
        nodeLinkedList.add(linked);
        return true;
    }

    public NodeParameter findParameter(UUID id) {
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            ObservableList<NodeParameter> parameters = node.getParameters();
            for (int j = 0; j < parameters.size(); j++) {
                NodeParameter parameter = parameters.get(j);
                if (parameter.getId().equals(id)) {
                    return parameter;
                }
            }
        }
        return null;
    }

    public ObjectProperty<NodeLinkStyle> linkStyleProperty() {
        return linkStyle;
    }

    public ObservableList<NodeGroup> getGroupList() {
        return groupList;
    }

    public ObservableList<NodeLinked> getNodeLinkedList() {
        return nodeLinkedList;
    }

    public ObservableList<Node> getNodeList() {
        return nodeList;
    }

    public NodeCanvasHandler getHandler() {
        return handler;
    }

    public void loadState(NodeCanvasState state) {
        offsetX.set(state.offsetX);
        offsetY.set(state.offsetY);
        scale.set(state.scale);
        state.nodeStateList.forEach(nodeState -> {
            nodeList.add(new Node(this, nodeState));
        });
        state.nodeLinkedStateList.forEach(nodeLinkedState -> {
            NodeLinked nodeLinked = new NodeLinked(this, nodeLinkedState);
            if (nodeLinked.getSource() != null && nodeLinked.getTarget() != null) {
                nodeLinkedList.add(nodeLinked);
            }
        });
        state.groupStateList.forEach(groupState -> {
            groupList.add(new NodeGroup(this, groupState));
        });
    }

    public NodeCanvasState saveState() {
        NodeCanvasState state = new NodeCanvasState();
        state.offsetX = offsetX.get();
        state.offsetY = offsetY.get();
        state.scale = scale.get();
        nodeList.stream().map(Node::saveState).forEach(state.nodeStateList::add);
        nodeLinkedList.stream().map(NodeLinked::saveState).forEach(state.nodeLinkedStateList::add);
        groupList.stream().map(NodeGroup::saveState).forEach(state.groupStateList::add);
        return state;
    }

    public class NodeCanvasNodeListListener implements ListChangeListener<Node> {
        @Override
        public void onChanged(Change<? extends Node> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(node -> {
                        node.initialize(NodeCanvas.this);
                        skin.onNodeAdded(node);
                    });
                }
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(skin::onNodeRemoved);
                }
            }
        }

    }

    public class NodeCanvasNodeGroupListListener implements ListChangeListener<NodeGroup> {
        @Override
        public void onChanged(Change<? extends NodeGroup> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(group -> {
                        group.initialize(NodeCanvas.this);
                        skin.onGroupAdded(group);
                    });
                }
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(skin::onGroupRemoved);
                }
            }
        }
    }

    public class NodeCanvasNodeLinkedListListener implements ListChangeListener<NodeLinked> {
        @Override
        public void onChanged(Change<? extends NodeLinked> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(skin::onLinkAdded);
                }
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(skin::onLinkRemoved);
                }
            }
        }
    }

    public class NodeCanvasLinkStyleListener implements ChangeListener<NodeLinkStyle> {
        @Override
        public void changed(ObservableValue<? extends NodeLinkStyle> observable, NodeLinkStyle oldValue, NodeLinkStyle newValue) {
            nodeLinkedList.forEach(nodeLinked -> {
                nodeLinked.styleHandlerProperty().set(newValue.createHandler());
            });
        }
    }
}
