package thito.nodeflow.internal.node.provider.java;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.reflectedbytecode.jvm.*;

public class SetArrayElement extends AbstractNodeProvider {
    public SetArrayElement(Class<?> clazz, int dimension, NodeProviderCategory category) {
        super("java#set_array://"+clazz.getName()+"("+dimension+")", "Set Array Element", category);
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.MULTIPLE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Array", clazz, true, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Index", int.class, false, LinkMode.SINGLE, LinkMode.NONE));
        addParameter(new JavaNodeParameter("Value", clazz.getComponentType(), false, LinkMode.SINGLE, LinkMode.NONE));
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                getHandler().prepareLocalField(this, getNode().getParameter(1));
                getHandler().prepareLocalField(this, getNode().getParameter(2));
                getHandler().prepareLocalField(this, getNode().getParameter(3));
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                Java.SetArrayElement(getHandler().getReference(getNode().getParameter(1)),
                        getHandler().getReference(getNode().getParameter(2)),
                        getHandler().getReference(getNode().getParameter(3)));
                getHandler().compile(findOutputNode(getNode().getParameter(0)));
            }
        };
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return new NodeCompileSession() {
            @Override
            public void handleCompile(NodeCompileHandler handler) {
                handler.getReference((node.getParameter(1)))
                        .arraySet(handler.getReference(node.getParameter(2)),
                                handler.getReference(node.getParameter(3)));
                handler.compile(findOutputNode(node.getParameter(0)));
            }
        };
    }

}
