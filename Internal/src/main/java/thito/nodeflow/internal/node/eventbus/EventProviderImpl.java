package thito.nodeflow.internal.node.eventbus;

import javafx.collections.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.library.binding.*;
import thito.reflectedbytecode.*;

import java.util.*;

public class EventProviderImpl extends AbstractNodeProvider implements EventProvider {

    private ObservableList<EventParameter> parameters = FXCollections.observableArrayList();

    public EventProviderImpl(String id, String name, NodeProviderCategory category) {
        super(id, name, category);
        MappedListBinding.bind(getParameters(), parameters, x -> {
            if (x == null) {
                return new ExecutionNodeParameter("Execution", LinkMode.NONE, LinkMode.SINGLE);
            } else {
                return new JavaNodeParameter(x.getName(), x.getType(), true, LinkMode.NONE, LinkMode.MULTIPLE);
            }
        });
        getEventParameters().add(null);
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                for (int i = 0; i < getEventParameters().size(); i++) {
                    if (parameters.get(i) != null) {
                        getHandler().skipLocal(getNode().getParameter(i));
                    }
                }
                for (int i = 0; i < getEventParameters().size(); i++) {
                    if (getEventParameters().get(i) == null) {
                        getHandler().prepareStructure(findOutputNode(getNode().getParameter(i)));
                    }
                }
            }

            @Override
            protected void handleCompile() {
                for (int i = 1; i < parameters.size(); i++) {
                    int index = i - 1;
                    getHandler().setDirectReference(getNode().getParameter(i), Code.getCode().getLocalFieldMap().getField(index).get());
                }
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public List<EventParameter> getEventParameters() {
        return parameters;
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                for (int i = 1; i < parameters.size(); i++) {
                    handler.putReference(node.getParameter(i), handler.getParameterReference(i));
                }
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

    @Override
    public Node fromState(NodeModule module, ComponentState state) {
        if (module instanceof HeadlessNodeModule) {
            HeadlessEventNode component = new HeadlessEventNode(module, state);
            for (int i = 0; i < state.getParameters().length; i++) {
                HeadlessNodeParameter parameter = new HeadlessNodeParameter(component, getParameters().get(i).getClass(), getParameters().get(i).getType(), state.getParameters()[i]);
                component.getParameters().add(parameter);
            }
            return component;
        }
        EventNodeImpl component = new EventNodeImpl(module, state, this);
        for (int i = 0; i < state.getParameters().length; i++) {
            component.getParameters().add(getParameters().get(i).createParameter(component, state.getParameters()[i]));
        }
        return component;
    }
}
