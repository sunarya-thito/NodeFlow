package thito.nodeflow.internal.node.provider.minecraft;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodejfx.*;

import java.lang.reflect.*;

public class ChestSlotParameter extends NodeParameterImpl {
    public ChestSlotParameter(ComponentParameterState state, String name, Node node, ParameterEditor editor, NodeParameterType type, Type genericType) {
        super(state, name, node, editor, type, genericType);
    }
}
