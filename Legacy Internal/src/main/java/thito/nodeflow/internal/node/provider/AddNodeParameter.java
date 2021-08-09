package thito.nodeflow.internal.node.provider;

import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;

public class AddNodeParameter implements NodeParameterFactory {

    @Override
    public String getName() {
        return "Add";
    }

    @Override
    public NodeParameter createParameter(Node node, ComponentParameterState state) {
        return new Add(state, getName(), node);
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public LinkMode getInputMode() {
        return LinkMode.NONE;
    }

    @Override
    public LinkMode getOutputMode() {
        return LinkMode.NONE;
    }

    public class Add extends NodeParameterImpl {
        public Add(ComponentParameterState state, String name, Node node) {
            super(state, name, node, param -> new AddParameter(), NodeParameterType.DEFAULT_TYPE, null);
        }

        public AddNodeParameter getFactory() {
            return AddNodeParameter.this;
        }
    }
}
