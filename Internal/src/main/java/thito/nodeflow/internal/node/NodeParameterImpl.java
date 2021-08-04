package thito.nodeflow.internal.node;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.node.eventbus.command.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.ui.*;
import thito.nodejfx.*;
import thito.nodejfx.event.*;
import thito.nodejfx.parameter.*;

import java.lang.reflect.*;
import java.util.*;

public class NodeParameterImpl implements NodeParameter {

//    private static final ComponentParameterState EMPTY_STATE = new ComponentParameterStateImpl(UUID.randomUUID(), null);

    protected Node node;
    protected ComponentParameterState state;
    protected String name;
    protected ParameterEditor editor;
    protected LinkMode inputMode = LinkMode.NONE, outputMode = LinkMode.NONE;
    protected NodeParameterType type;
    protected thito.nodejfx.NodeParameter parameter;
    protected boolean removable;
    private Type genericType;

    public NodeParameterImpl(ComponentParameterState state, String name, Node node, ParameterEditor editor, NodeParameterType type, Type genericType) {
        this.state = state;
        this.name = name;
        this.type = type;
        this.node = node;
        this.genericType = genericType;
        setEditor(editor);
    }

    public Type getGenericType() {
        return genericType;
    }

    public void setGenericType(Type genericType) {
        this.genericType = genericType;
    }

    public void setName(String name) {
        this.name = name;
        updateName();
    }

    private void updateName() {
        if (parameter instanceof UserInputParameter) {
            ((UserInputParameter<?>) parameter).setName(name);
        }
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
        parameter.setRemovable(removable);
    }

    public void setState(ComponentParameterState state) {
        this.state = state;
    }

    public thito.nodejfx.NodeParameter getParameter() {
        return parameter;
    }

    public void setEditor(ParameterEditor editor) {
        if (this.editor != null && editor.getClass() == this.editor.getClass()) {
            if (editor.getClass() == ModuleManagerImpl.FallbackEditor.class) {
                ((SpecificParameter) this.parameter).setSubName(ModuleManagerImpl.parameterizedTypeSimpleName(
                        ((ModuleManagerImpl.FallbackEditor) editor).getStorage(),
                        ((ModuleManagerImpl.FallbackEditor) editor).getGeneric()));
            }
            return;
        }
        this.editor = editor;
        int index = ((NodeImpl) node).impl_getPeer().getParameters().indexOf(getParameter());
        setupParameter((thito.nodejfx.NodeParameter) editor.createPeer(this));
        if (index >= 0) {
            ((NodeImpl) node).impl_getPeer().getParameters().set(index, getParameter());
        }
    }

    public void setType(NodeParameterType type) {
        this.type = type;
        updateType();
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
    public thito.nodejfx.NodeParameter impl_getPeer() {
        return parameter;
    }
    protected void updateType() {
        this.parameter.getInputType().set(type);
        this.parameter.getOutputType().set(type);
        if (this.parameter instanceof SpecificParameter) {
            GenericTypeStorage storage = node instanceof NodeImpl ? ((NodeImpl) node).getStorage() : null;
            ((SpecificParameter) this.parameter).setSubName(ModuleManagerImpl.parameterizedTypeSimpleName(storage, getGenericType()));
        }
        if (getGenericType() != null) {
            GenericTypeStorage storage = node instanceof NodeImpl ? ((NodeImpl) node).getStorage() : null;
            Toolkit.install(parameter, () -> new DocsUI((Stage) parameter.getScene().getWindow(), JavaNodeParameter.fromGeneric(storage, getGenericType())), true);
        }
        if (type != NodeParameterType.DEFAULT_TYPE) {
            this.parameter.setInputShape(NodeLinkShape.CIRCLE_SHAPE);
            this.parameter.setOutputShape(NodeLinkShape.CIRCLE_SHAPE);
        }
    }
    private void updateInputMode() {
        switch (getInputMode()) {
            case NONE:
                parameter.getAllowInput().set(false);
                parameter.getMultipleInputAssigner().set(false);
                break;
            case SINGLE:
                parameter.getAllowInput().set(true);
                parameter.getMultipleInputAssigner().set(false);
                break;
            case MULTIPLE:
                parameter.getAllowInput().set(true);
                parameter.getMultipleInputAssigner().set(true);
                break;
        }
    }
    private void updateOutputMode() {
        switch (getOutputMode()) {
            case NONE:
                parameter.getAllowOutput().set(false);
                parameter.getMultipleOutputAssigner().set(false);
                break;
            case SINGLE:
                parameter.getAllowOutput().set(true);
                parameter.getMultipleOutputAssigner().set(false);
                break;
            case MULTIPLE:
                parameter.getAllowOutput().set(true);
                parameter.getMultipleOutputAssigner().set(true);
                break;
        }
    }

    protected void setupParameter(thito.nodejfx.NodeParameter parameter) {
        this.parameter = parameter;
        parameter.setUserData(this);
        parameter.getInputType().set(type);
        parameter.getOutputType().set(type);
        updateType();
        updateInputMode();
        updateOutputMode();
        parameter.removableProperty().set(removable);
        if (parameter instanceof UserInputParameter) {
            if (state != null) {
                ObjectProperty<Object> value = ((UserInputParameter<?>) parameter).valueProperty();
                value.set(state.getConstantValue());
                value.addListener((obs, old, val) -> {
                    state.setConstantValue(val);
                });
            }
            parameter.getUnmodifiableInputLinks().addListener((SetChangeListener<thito.nodejfx.NodeParameter>) change -> {
                javafx.scene.Node input = ((UserInputParameter<?>) parameter).getInputComponent();
                if (input != null) {
                    input.setVisible(change.getSet().isEmpty());
                }
            });
        }
        parameter.addEventHandler(NodeLinkEvent.NODE_LINKED_EVENT, event -> {
            if (event.getNodeInput() == parameter) { // only fired once!
                NodeModule module = node.getModule();
                if (((NodeFileViewportImpl) event.getNodeInput().getCanvas().getUserData()).updatingLinks) return;
                ((NodeFileViewportImpl) event.getNodeInput().getCanvas().getUserData()).updatingLinks = true;
                NodeParameter from = (NodeParameter) event.getNodeOutput().getUserData();
                NodeParameter to = (NodeParameter) event.getNodeInput().getUserData();
                NodeReachableHandler handler = NodeReachableHandler.get(((NodeImpl) from.getNode()).impl_getPeer());
                if (!handler.hasTheSameRoot(((NodeImpl) to.getNode()).impl_getPeer())) {
                    event.consume();
                    ((NodeFileViewportImpl) event.getNodeInput().getCanvas().getUserData()).updatingLinks = false;
                    return;
                }
                LinkImpl link = new LinkImpl(module, from.getID(), to.getID());
                link.setTarget(to);
                link.setSource(from);
                link.impl_setPeer(event.getLinked());
                NodeFileSession session = ((StandardNodeModule) module).getSession();
                if (session != null) {
                    event.getLinked().getStyle().setActive(session.alwaysAnimateProperty().get());
                }
                ((StandardNodeModule) module).links().add(link);
                ((NodeFileViewportImpl) event.getNodeInput().getCanvas().getUserData()).updatingLinks = false;
            }
        });
        parameter.addEventHandler(NodeLinkEvent.NODE_UNLINKED_EVENT, event -> {
            if (event.getNodeOutput() == parameter) { // only fired once!
                NodeModule module = node.getModule();
                if (((NodeFileViewportImpl) event.getNodeInput().getCanvas().getUserData()).updatingLinks) return;
                ((NodeFileViewportImpl) event.getNodeInput().getCanvas().getUserData()).updatingLinks = true;
                ((StandardNodeModule) module).links().removeIf(link -> link.getSource() == event.getNodeOutput().getUserData() && link.getTarget() == event.getNodeInput().getUserData());
                ((NodeFileViewportImpl) event.getNodeInput().getCanvas().getUserData()).updatingLinks = false;
            }
        });
        if (parameter instanceof SpecificParameter && parameter.getAllowInput().get()) {
            InvalidationListener check = obs -> {
                if (parameter.getUnmodifiableInputLinks().size() > 0) {
                    ((UserInputParameter<?>) parameter).getLabel().textFillProperty().set(Color.WHITE);
                } else {
                    ((UserInputParameter<?>) parameter).getLabel().textFillProperty().set(Color.GOLD);
                }
            };
            parameter.getUnmodifiableInputLinks().addListener(check);
            check.invalidated(null);
        }
    }

    @Override
    public ComponentParameterState getState() {
        return state;
    }

    @Override
    public Object impl_getType() {
        return type;
    }

    @Override
    public UUID getID() {
        return state == null ? new UUID(0, 0) : state.getID();
    }

    @Override
    public void setInputMode(LinkMode mode) {
        inputMode = mode;
        updateInputMode();
    }

    @Override
    public void setOutputMode(LinkMode mode) {
        outputMode = mode;
        updateOutputMode();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Object getValue() {
        return state == null ? null : state.getConstantValue();
    }

    @Override
    public void setValue(Object value) {
        if (state != null) {
            state.setConstantValue(value);
        }
    }

    @Override
    public ParameterEditor getEditor() {
        return editor;
    }

    @Override
    public LinkMode getInputMode() {
        return inputMode;
    }

    @Override
    public LinkMode getOutputMode() {
        return outputMode;
    }
}
