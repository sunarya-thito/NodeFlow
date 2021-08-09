package thito.nodeflow.internal.node.provider;

import javafx.collections.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.node.headless.state.*;
import thito.nodeflow.internal.node.search.*;
import thito.nodeflow.internal.node.state.*;

import java.util.*;
import java.util.stream.*;

public abstract class AbstractNodeProvider implements NodeProvider {
    private String id,name;
    private NodeProviderCategory category;

    public AbstractNodeProvider(String id, String name, NodeProviderCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    protected void setID(String id) {
        this.id = id;
    }

    protected void setName(String name) {
        this.name = name;
    }

    private ObservableList<NodeParameterFactory> parameters = FXCollections.observableArrayList();
    @Override
    public String getID() {
        return id;
    }

    public ObservableList<NodeParameterFactory> getParameters() {
        return parameters;
    }

    public void setCategory(NodeProviderCategory category) {
        this.category = category;
    }

    public AbstractNodeProvider addParameter(NodeParameterFactory parameter) {
        parameters.add(parameter);
        return this;
    }

    protected boolean isParameterUsed(NodeParameter parameter) {
        return parameter.hasOutputLink();
    }

    protected Set<NodeParameter> findInputLinks(NodeParameter parameter) {
        return parameter.collectInputLinks();
    }

    protected Set<NodeParameter> findOutputLinks(NodeParameter parameter) {
        return parameter.collectOutputLinks();
    }

    protected Node findOutputNode(NodeParameter parameter) {
        NodeParameter out = parameter.getOutputLink();
        if (out != null) {
            return out.getNode();
        }
        return null;
    }

    public abstract CompileSession createNewSession();

    @Override
    public String getDescription() {
        return parameters.stream().filter(x -> x.getType() != null).map(x -> ModuleManagerImpl.toString(x.getType())).collect(Collectors.joining(", "));
    }

    public int getParameterIndex(boolean input, Class<?> type) {
        if (type == null) return getExecutionIndex(input);
        for (int i = 0; i < parameters.size(); i++) {
            NodeParameterFactory factory = parameters.get(i);
            if (input && factory.getInputMode() != LinkMode.NONE) {
                if (factory.getType() == type || (type != null && factory.getType() != null && ExpectingType.isAssignableFrom(factory.getType(), type))) {
                    return i;
                }
            } else if (!input && factory.getOutputMode() != LinkMode.NONE) {
                if (factory.getType() == type || (type != null && factory.getType() != null && ExpectingType.isAssignableFrom(type, factory.getType()))) {
                    return i;
                }
            }
        }
        for (int i = 0; i < parameters.size(); i++) {
            NodeParameterFactory factory = parameters.get(i);
            if (input && factory.getInputMode() != LinkMode.NONE) {
                if (factory.getType() == type || (type != null && factory.getType() != null && ExpectingType.isAssignableFrom(type, factory.getType()))) {
                    return i;
                }
            } else if (!input && factory.getOutputMode() != LinkMode.NONE) {
                if (factory.getType() == type || (type != null && factory.getType() != null && ExpectingType.isAssignableFrom(factory.getType(), type))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getImplementationInput(boolean input) {
        for (int i = 0; i < parameters.size(); i++) {
            NodeParameterFactory factory = parameters.get(i);
            if (factory instanceof MethodOverrideNodeParameter) {
                return i;
            }
        }
        return -4;
    }

    public int getExecutionIndex(boolean input) {
        for (int i = 0; i < parameters.size(); i++) {
            NodeParameterFactory factory = parameters.get(i);
            if (factory instanceof ExecutionNodeParameter) {
                if (!input ? factory.getInputMode() != LinkMode.NONE : factory.getOutputMode() != LinkMode.NONE) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public NodeProviderCategory getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node createComponent(NodeModule module) {
        if (module instanceof HeadlessNodeModule) {
            HeadlessComponentStateImpl state = new HeadlessComponentStateImpl((HeadlessNodeModule) module);
            state.setProvider(this);
            state.setParameters(parameters.stream().map(
                    x -> new HeadlessComponentParameterStateImpl((HeadlessNodeModule) module, UUID.randomUUID(), null)
            ).toArray(ComponentParameterState[]::new));
            return fromState(module, state);
        }
        ComponentStateImpl state = new ComponentStateImpl((StandardNodeModule) module, id);
        state.setProvider(this);
        state.setParameters(parameters.stream().map(x -> new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null)).toArray(ComponentParameterState[]::new));
        return fromState(module, state);
    }

    @Override
    public Node fromState(NodeModule module, ComponentState state) {
        if (module instanceof HeadlessNodeModule) {
            HeadlessNode component = new HeadlessNode(module, state);
            for (int i = 0; i < state.getParameters().length; i++) {
                HeadlessNodeParameter parameter = new HeadlessNodeParameter(component, this.parameters.get(i).getClass(), this.parameters.get(i).getType(), state.getParameters()[i]);
                component.getParameters().add(parameter);
            }
            return component;
        }
        NodeImpl component = createNode(module, state);
        for (int i = 0; i < state.getParameters().length; i++) {
            component.getParameters().add(this.parameters.get(i).createParameter(component, state.getParameters()[i]));
        }
        return component;
    }

    protected NodeImpl createNode(NodeModule module, ComponentState state) {
        return new NodeImpl(module, state, this);
    }
}
