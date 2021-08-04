package thito.nodeflow.internal.node.headless;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;

import java.util.*;

public class HeadlessNodeParameter implements NodeParameter {
    private Node node;
    private ComponentParameterState state;
    private Class<?> realParameterType, contentType;

    public HeadlessNodeParameter(Node node, Class<?> realParameterType, Class<?> contentType, ComponentParameterState state) {
        this.node = node;
        this.state = state;
        this.realParameterType = realParameterType;
        this.contentType = contentType;
    }

    public Class<?> getContentType() {
        return contentType;
    }

    public Class<?> getRealParameterType() {
        return realParameterType;
    }

    @Override
    public NodeParameter getOutputLink() {
        for (Link link : node.getModule().getLinks()) {
            if (link.getSource() == this) {
                return link.getTarget();
            }
        }
        return null;
    }

    @Override
    public NodeParameter getInputLink() {
        for (Link link : node.getModule().getLinks()) {
            if (link.getTarget() == this) {
                return link.getSource();
            }
        }
        return null;
    }

    @Override
    public boolean hasOutputLink() {
        for (Link link : node.getModule().getLinks()) {
            if (link.getSource() == this) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasInputLink() {
        for (Link link : node.getModule().getLinks()) {
            if (link.getTarget() == this) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<NodeParameter> collectOutputLinks() {
        Set<NodeParameter> parameters = new HashSet<>();
        for (Link link : node.getModule().getLinks()) {
            if (link.getSource() == this) {
                parameters.add(link.getTarget());
            }
        }
        return parameters;
    }

    @Override
    public Set<NodeParameter> collectInputLinks() {
        Set<NodeParameter> parameters = new HashSet<>();
        for (Link link : node.getModule().getLinks()) {
            if (link.getTarget() == this) {
                parameters.add(link.getSource());
            }
        }
        return parameters;
    }

    @Override
    public UUID getID() {
        return state.getID();
    }

    @Override
    public String getName() {
        return state.getID().toString();
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Object getValue() {
        return state.getConstantValue();
    }

    @Override
    public void setValue(Object value) {
        state.setConstantValue(value);
    }

    @Override
    public ParameterEditor getEditor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInputMode(LinkMode mode) {
    }

    @Override
    public void setOutputMode(LinkMode mode) {
    }

    @Override
    public LinkMode getInputMode() {
        return LinkMode.NONE;
    }

    @Override
    public LinkMode getOutputMode() {
        return LinkMode.NONE;
    }

    @Override
    public Object impl_getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object impl_getPeer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentParameterState getState() {
        return state;
    }
}
