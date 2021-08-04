package thito.nodeflow.internal.node;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodejfx.parameter.type.*;
import thito.reflectedbytecode.jvm.*;

public class UnknownProvider extends AbstractNodeProvider {

    public static final NodeProviderCategory UNKNOWN = new SimpleNodeProviderCategory("?", "?", null);
    public UnknownProvider(String id) {
        super(id, "?", UNKNOWN);
    }

    @Override
    public Node fromState(NodeModule module, ComponentState state) {
        if (module instanceof HeadlessNodeModule) {
            HeadlessNode component = new HeadlessNode(module, state);
            for (int i = 0; i < state.getParameters().length; i++) {
                HeadlessNodeParameter parameter = new HeadlessNodeParameter(component, UnknownParameter.class, Object.class, state.getParameters()[i]);
                component.getParameters().add(parameter);
            }
            return component;
        }
        NodeImpl component = new NodeImpl(module, state, this);
        for (int i = 0; i < state.getParameters().length; i++) {
            component.getParameters().add(new UnknownParameter(state.getParameters()[i], component));
        }
        return component;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                throw new IllegalStateException("Missing Provider: "+getNode().getState().getProviderID());
            }

            @Override
            protected void handleCompile() {
                throw new IllegalStateException("Missing Provider: "+getNode().getState().getProviderID());
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            protected void handleCompile(NodeCompileHandler handler) {
                for (NodeParameter parameter : node.getParameters()) {
                    handler.putReference(parameter, Java.Null());
                }
            }
        };
    }

    public static class UnknownParameter extends NodeParameterImpl {
        public UnknownParameter(ComponentParameterState state, Node node) {
            super(state, "?", node, ModuleManagerImpl.getInstance().getFallthroughEditor(Object.class), new JavaParameterType.UnknownParameterType(), null);
            setInputMode(LinkMode.MULTIPLE);
            setOutputMode(LinkMode.MULTIPLE);
        }
    }
}
