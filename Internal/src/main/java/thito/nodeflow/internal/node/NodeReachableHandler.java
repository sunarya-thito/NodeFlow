package thito.nodeflow.internal.node;

import javafx.beans.*;
import javafx.collections.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodejfx.*;

import java.util.*;

public class NodeReachableHandler {

    public static NodeReachableHandler get(Node node) {
        return (NodeReachableHandler) node.getProperties().get(NodeReachableHandler.class);
    }

    private boolean root;
    private Node node;

    public NodeReachableHandler(Node node) {
        this.node = node;
        root = node.getUserData() instanceof NodeImpl && (((NodeImpl) node.getUserData()).getState().getProvider() instanceof EventProvider || ((NodeImpl) node.getUserData()).getState().getProvider() instanceof CommandNodeProvider);
        node.getProperties().put(NodeReachableHandler.class, this);
        initialize();
    }

    public boolean isRoot() {
        return root;
    }

    private void bulkRoot(Set<Node> roots, Set<NodeReachableHandler> skip) {
        if (skip.add(this)) {
            if (root) {
                roots.add(node);
            }
            for (NodeParameter parameter : node.getParameters()) {
                for (NodeParameter input : parameter.getUnmodifiableInputLinks()) {
                    Node other = input.getNode();
                    NodeReachableHandler handler = get(other);
                    if (handler != null) {
                        handler.bulkRoot(roots, skip);
                    }
                }
                for (NodeParameter output : parameter.getUnmodifiableOutputLinks()) {
                    Node other = output.getNode();
                    NodeReachableHandler handler = get(other);
                    if (handler != null) {
                        handler.bulkRoot(roots, skip);
                    }
                }
            }
        }
    }
    private Set<Node> collectRoot(Set<NodeReachableHandler> skip) {
        if (skip.contains(this)) return new HashSet<>(0);
        skip.add(this);
        if (root) {
            Set<Node> roots = new HashSet<>(1);
            roots.add(node);
            return roots;
        }
        Set<Node> roots = new HashSet<>();
        for (NodeParameter parameter : node.getParameters()) {
            if (parameter.getUserData() instanceof ExecutionNodeParameter.ExecutionParameter || parameter.getUserData() instanceof MethodOverrideNodeParameter.MethodOverrideParameter) {
                for (NodeParameter linked : parameter.getUnmodifiableInputLinks()) {
                    Node other = linked.getNode();
                    NodeReachableHandler handler = get(other);
                    if (handler != null) {
                        roots.addAll(handler.collectRoot(skip));
                    }
                }
            } else {
                for (NodeParameter linked : parameter.getUnmodifiableOutputLinks()) {
                    Node other = linked.getNode();
                    NodeReachableHandler handler = get(other);
                    if (handler != null) {
                        roots.addAll(handler.collectRoot(skip));
                    }
                }
            }
        }
        return roots;
    }
    private InvalidationListener listener;

    private void check() {
        linkedCheck(new HashSet<>());
    }

    private void linkedCheck(Set<NodeReachableHandler> nodes) {
        Set<NodeReachableHandler> used = new HashSet<>();
        node.reachableInputProperty().set(!collectRoot(used).isEmpty());
        if (nodes.add(this)) {
            for (NodeParameter parameter : node.getParameters()) {
                for (NodeParameter linked : parameter.getUnmodifiableInputLinks()) {
                    Node other = linked.getNode();
                    NodeReachableHandler handler = get(other);
                    if (handler != null) {
                        handler.linkedCheck(nodes);
                    }
                }
                for (NodeParameter linked : parameter.getUnmodifiableOutputLinks()) {
                    Node other = linked.getNode();
                    NodeReachableHandler handler = get(other);
                    if (handler != null) {
                        handler.linkedCheck(nodes);
                    }
                }
            }
        }
    }

    public boolean hasTheSameRoot(Node node) {
        Set<Node> roots = new HashSet<>();
        Set<Node> otherRoots = new HashSet<>();
        bulkRoot(roots, new HashSet<>());
        NodeReachableHandler handler = get(node);
        if (handler != null) {
            handler.bulkRoot(otherRoots, new HashSet<>());
        }
        return roots.isEmpty() || otherRoots.isEmpty() || otherRoots.containsAll(roots);
    }

    private void initialize() {
        if (root) {
            node.reachableInputProperty().set(true);
            return;
        }
        listener = obs -> {
            check();
        };
        node.getParameters().addListener((ListChangeListener<NodeParameter>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (NodeParameter parameter : c.getAddedSubList()) {
                        parameter.getUnmodifiableInputLinks().addListener(listener);
                        parameter.getUnmodifiableOutputLinks().addListener(listener);
                    }
                }
                if (c.wasRemoved()) {
                    for (NodeParameter parameter : c.getRemoved()) {
                        parameter.getUnmodifiableInputLinks().removeListener(listener);
                        parameter.getUnmodifiableOutputLinks().removeListener(listener);
                    }
                }
            }
        });
        listener.invalidated(null);
    }

}
